package com.jtbdevelopment.games.mongo.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration;
import com.jtbdevelopment.core.mongo.spring.AbstractMongoNoSpringContextIntegration;
import com.jtbdevelopment.core.mongo.spring.MongoProperties;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.dao.caching.CacheConstants;
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.mongo.state.utility.SimpleSinglePlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.mongodb.client.MongoCollection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

/**
 * Date: 1/10/15 Time: 2:35 PM
 */
@SuppressWarnings({"ConstantConditions", "unchecked"})
public class MongoSinglePlayerGamesIntegration extends AbstractMongoNoSpringContextIntegration {

  private static final String GAMES_COLLECTION_NAME = "single";
  private static ApplicationContext context;
  private MongoCollection collection;
  private MongoPlayerRepository playerRepository;
  private AbstractSinglePlayerGameRepository gameRepository;
  private MongoPlayer player1;
  private MongoPlayer player2;
  private CacheManager cacheManager;
  private Cache cache;
  private Instant start;

  @BeforeClass
  public static void setupAll() throws Exception {
    AbstractMongoNoSpringContextIntegration.setupMongo();
    context = new AnnotationConfigApplicationContext(
        MongoSinglePlayerGameIntegrationConfiguration.class);
  }

  @AfterClass
  public static void tearDownAll() throws Exception {
    AbstractMongoNoSpringContextIntegration.tearDownMongo();
  }

  @Before
  public void setup() {
    start = Instant.now();
    collection = db.getCollection(GAMES_COLLECTION_NAME);

    playerRepository = context.getBean(MongoPlayerRepository.class);
    gameRepository = context.getBean(AbstractSinglePlayerGameRepository.class);

    gameRepository.deleteAll();
    playerRepository.deleteAll();

    MongoPlayer player = new MongoPlayer();
    player.setSource("MANUAL");
    player.setSourceId("MAN1");
    player1 = playerRepository.save(player);
    MongoPlayer player1 = new MongoPlayer();
    player1.setSource("MANUAL");
    player1.setSourceId("MAN2");
    player2 = (MongoPlayer) playerRepository.save(player1);

    cacheManager = context.getBean(CacheManager.class);
    cache = cacheManager.getCache(CacheConstants.GAME_ID_CACHE);
  }

  @Test
  public void testCanCreateGameAndReloadIt() {
    SimpleSinglePlayerGame saved;
    SimpleSinglePlayerGame loaded;
    SimpleSinglePlayerGame save = new SimpleSinglePlayerGame();

    save.setIntValue(5);
    save.setStringValue("X");
    save.setPlayer(player1);
    save.setFeatures(new HashSet<>(Arrays.asList("GG", "A")));
    Map<String, Object> featureData = new HashMap<>();
    featureData.put("H", "N");
    save.setFeatureData(featureData);
    assertNull(save.getId());
    assertNull(save.getCreated());
    assertNull(save.getLastUpdate());
    assertNull(save.getCompletedTimestamp());
    saved = (SimpleSinglePlayerGame) gameRepository.save(save);
    Assert.assertNotNull(saved);
    Assert.assertNotNull(saved.getId());
    Assert.assertNotNull(saved.getLastUpdate());
    Assert.assertNotNull(saved.getCreated());
    assertEquals(save.getIntValue(), saved.getIntValue());
    assertEquals(save.getStringValue(), saved.getStringValue());
    assertEquals(save.getPlayer(), saved.getPlayer());
    assertNull(saved.getCompletedTimestamp());
    assertEquals(save.getFeatureData(), saved.getFeatureData());
    assertEquals(save.getFeatures(), saved.getFeatures());

    loaded = (SimpleSinglePlayerGame) gameRepository.findById(saved.getId()).get();
    Assert.assertNotNull(loaded);
    assertEquals(saved.getId(), loaded.getId());
    assertEquals(saved.getLastUpdate(), loaded.getLastUpdate());
    assertEquals(save.getCreated(), loaded.getCreated());
    assertEquals(save.getIntValue(), loaded.getIntValue());
    assertEquals(save.getStringValue(), loaded.getStringValue());
    assertEquals(save.getPlayer(), loaded.getPlayer());
    assertNull(loaded.getCompletedTimestamp());
    assertEquals(save.getFeatureData(), loaded.getFeatureData());
    assertEquals(save.getFeatures(), loaded.getFeatures());

    assertEquals(1, gameRepository.count());
    assertEquals(1, gameRepository.countByCreatedGreaterThan(start.minusSeconds(1)));
    assertEquals(0, gameRepository.countByCreatedGreaterThan(loaded.getCreated()));
  }

  @Test
  public void testCanUpdateAGame() {
    SimpleSinglePlayerGame update;
    SimpleSinglePlayerGame updated;
    SimpleSinglePlayerGame loaded;
    SimpleSinglePlayerGame initial = new SimpleSinglePlayerGame();
    initial.setIntValue(5);
    initial.setStringValue("X");
    initial.setPlayer(player1);
    initial = (SimpleSinglePlayerGame) gameRepository.save(initial);

    update = (SimpleSinglePlayerGame) gameRepository.findById(initial.getId()).get();
    update.setStringValue(update.getStringValue() + "Z");
    update.setCompletedTimestamp(Instant.now());
    update.setIntValue(update.getIntValue() * 2);
    update.getFeatures().addAll(new ArrayList<>(Arrays.asList("HG", "34")));
    update.getFeatureData().put("rr", new Long(3));
    updated = (SimpleSinglePlayerGame) gameRepository.save(update);
    Assert.assertNotNull(updated);
    assertEquals(initial.getId(), updated.getId());
    Assert.assertTrue(updated.getLastUpdate().compareTo(initial.getLastUpdate()) > 0);
    assertEquals(initial.getCreated(), updated.getCreated());
    assertEquals(update.getIntValue(), updated.getIntValue());
    assertEquals(update.getStringValue(), updated.getStringValue());
    assertEquals(update.getPlayer(), updated.getPlayer());
    assertEquals(update.getCompletedTimestamp(), updated.getCompletedTimestamp());
    assertEquals(update.getFeatureData(), updated.getFeatureData());
    assertEquals(update.getFeatures(), updated.getFeatures());

    loaded = (SimpleSinglePlayerGame) gameRepository.findById(update.getId()).get();
    Assert.assertNotNull(loaded);
    assertEquals(updated.getId(), loaded.getId());
    assertEquals(updated.getLastUpdate(), loaded.getLastUpdate());
    assertEquals(updated.getCreated(), loaded.getCreated());
    assertEquals(updated.getIntValue(), loaded.getIntValue());
    assertEquals(updated.getStringValue(), loaded.getStringValue());
    assertEquals(updated.getPlayer(), loaded.getPlayer());
    assertEquals(updated.getCompletedTimestamp(), loaded.getCompletedTimestamp());
    assertEquals(update.getFeatureData(), updated.getFeatureData());
    assertEquals(update.getFeatures(), updated.getFeatures());

    assertEquals(1, gameRepository.count());
  }

  @Test
  public void testFindGamesByPlayer() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    game.setGamePhase(GamePhase.Playing);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(10);
    game1.setStringValue("X");
    game1.setPlayer(player1);
    game1.setGamePhase(GamePhase.Playing);
    SimpleSinglePlayerGame p1g2 = (SimpleSinglePlayerGame) gameRepository.save(game1);

    SimpleSinglePlayerGame game2 = new SimpleSinglePlayerGame();
    game2.setIntValue(15);
    game2.setStringValue("2");
    game2.setPlayer(player1);
    game2.setGamePhase(GamePhase.Challenged);
    SimpleSinglePlayerGame p1g3 = (SimpleSinglePlayerGame) gameRepository.save(game2);

    SimpleSinglePlayerGame game3 = new SimpleSinglePlayerGame();
    game3.setIntValue(20);
    game3.setStringValue("2");
    game3.setPlayer(player2);
    game3.setGamePhase(GamePhase.Challenged);
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game3);

    List<SimpleSinglePlayerGame> p1g = (List<SimpleSinglePlayerGame>) gameRepository
        .findByPlayerId(player1.getId());
    assertEquals(3, p1g.size());
    Assert.assertTrue(p1g.contains(p1g1));
    Assert.assertTrue(p1g.contains(p1g2));
    Assert.assertTrue(p1g.contains(p1g3));
    List<SimpleSinglePlayerGame> p2g = (List<SimpleSinglePlayerGame>) gameRepository
        .findByPlayerId(player2.getId());
    assertEquals(1, p2g.size());
    Assert.assertTrue(p2g.contains(p2g1));

    assertEquals(4,
        StreamSupport.stream(gameRepository.findAll().spliterator(), false).count());

    Sort sort = new Sort(Direction.DESC,
        new ArrayList<>(Arrays.asList("lastUpdate", "created")));
    PageRequest page = new PageRequest(0, 20, sort);
    List<SimpleSinglePlayerGame> by = (List<SimpleSinglePlayerGame>) gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(player1.getId(), GamePhase.Playing,
            p1g1.getCreated().minusSeconds(24 * 60 * 60), page);
    assertEquals(2, by.size());
    Assert.assertTrue(by.contains(p1g1));
    Assert.assertTrue(by.contains(p1g2));
  }

  @Test
  public void testSavesCache() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayer(player2);
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game1);

    gameRepository.save(p1g1);
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    p1g1.setIntValue(150);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(150, ((SimpleSinglePlayerGame) cache.get(p1g1.getId()).get()).getIntValue());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());
  }

  @Test
  public void testDeleteAllCache() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayer(player2);
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game1);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());

    gameRepository.deleteAll();

    assertNull(cache.get(p1g1.getId()));
    assertNull(cache.get(p2g1.getId()));
  }

  @Test
  public void testSingleDeletesForCache() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayer(player2);
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game1);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());

    gameRepository.delete(p1g1);
    gameRepository.deleteById(p2g1.getId());

    assertNull(cache.get(p1g1.getId()));
    assertNull(cache.get(p2g1.getId()));
  }

  @Test
  public void testFindsCreatedBefore() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayer(player2);
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game1);

    List<SimpleSinglePlayerGame> games;
    games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(p1g1.getCreated());
    Assert.assertFalse(games.contains(p1g1));
    Assert.assertFalse(games.contains(p2g1));

    games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(Instant.now());
    Assert.assertTrue(games.contains(p1g1));
    Assert.assertTrue(games.contains(p2g1));
  }

  @Test
  public void testDeleteCreatedBefore() throws InterruptedException {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    game.setCreated(Instant.now());
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    Thread.sleep(100);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayer(player2);
    game1.setCreated(Instant.now());
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game1);

    Assert.assertTrue(gameRepository.findById(p1g1.getId()).isPresent());
    Assert.assertTrue(gameRepository.findById(p2g1.getId()).isPresent());

    Assert.assertTrue(1 <= gameRepository.deleteByCreatedLessThan(p2g1.getCreated()));

    Assert.assertTrue(gameRepository.findById(p2g1.getId()).isPresent());
    Assert.assertFalse(gameRepository.findById(p1g1.getId()).isPresent());
  }

  @Test
  public void testIterableDeletesForCache() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);

    SimpleSinglePlayerGame game1 = new SimpleSinglePlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayer(player2);
    SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(game1);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());

    gameRepository.deleteAll(Arrays.asList(p1g1, p2g1));

    assertNull(cache.get(p1g1.getId()));
    assertNull(cache.get(p2g1.getId()));
  }

  @Test
  public void testCacheReallyHit() {
    SimpleSinglePlayerGame game = new SimpleSinglePlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayer(player1);
    SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(game);
    gameRepository.save(p1g1);
    assertEquals(p1g1, cache.get(p1g1.getId()).get());

    MongoOperations operations = context.getBean(MongoOperations.class);
    operations.remove(Query.query(Criteria.where("_id").is(p1g1.getId())), GAMES_COLLECTION_NAME);

    assertEquals(p1g1, gameRepository.findById(p1g1.getId()).get());

    cache.clear();
    assertNull(cache.get(p1g1.getId()));
    Assert.assertFalse(gameRepository.findById(p1g1.getId()).isPresent());
  }

  @Test
  public void testPlayerCount() {
    assertEquals(2L, playerRepository.count());
  }

  @Configuration
  @EnableMongoRepositories(basePackages = {"com.jtbdevelopment"}, excludeFilters = {
      @Filter(type = FilterType.REGEX, pattern = {
          "com.jtbdevelopment.games.mongo.state.utility.SimpleMultiPlayerGameRepository"})})
  @EnableMongoAuditing
  @ComponentScan(basePackages = {"com.jtbdevelopment"}, excludeFilters = {
      @Filter(type = FilterType.REGEX, pattern = {
          "com.jtbdevelopment.core.mongo.spring.social.dao.*IntegrationSocialConfiguration",
          "com.jtbdevelopment.*.*MongoMultiPlayerGameIntegrationConfiguration",
          "com.jtbdevelopment.*.*MongoPlayerIntegrationConfiguration",
          "com.jtbdevelopment.*.*CoreSpringConfiguration",
          "com.jtbdevelopment.*.*MongoConfiguration"})})
  public static class MongoSinglePlayerGameIntegrationConfiguration extends
      AbstractCoreMongoConfiguration {

    public MongoSinglePlayerGameIntegrationConfiguration(final List<MongoConverter> mongoConverters,
        final MongoProperties mongoProperties) {
      super(mongoConverters, mongoProperties);
    }

    @Bean
    public ConnectionFactoryRegistry connectionFactoryLocator() {
      return new ConnectionFactoryRegistry();
    }

    @Override
    protected String getMappingBasePackage() {
      Package mappingBasePackage = getClass().getPackage();
      return mappingBasePackage == null ? null : mappingBasePackage.getName();
    }

  }
}
