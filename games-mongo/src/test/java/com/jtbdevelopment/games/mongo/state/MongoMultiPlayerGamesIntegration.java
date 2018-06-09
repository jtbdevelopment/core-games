package com.jtbdevelopment.games.mongo.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration;
import com.jtbdevelopment.core.mongo.spring.AbstractMongoNoSpringContextIntegration;
import com.jtbdevelopment.core.mongo.spring.MongoProperties;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.caching.CacheConstants;
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.mongo.state.utility.SimpleMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import com.mongodb.client.MongoCollection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
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
@SuppressWarnings("ConstantConditions")
public class MongoMultiPlayerGamesIntegration extends AbstractMongoNoSpringContextIntegration {

  private static final String GAMES_COLLECTION_NAME = "multi";
  private static ApplicationContext context;
  private MongoCollection collection;
  private MongoPlayerRepository playerRepository;
  private AbstractMultiPlayerGameRepository gameRepository;
  private MongoPlayer player1;
  private MongoPlayer player2;
  private MongoPlayer player3;
  private MongoPlayer player4;
  private CacheManager cacheManager;
  private Cache cache;
  private Instant start;

  @BeforeClass
  public static void setupAll() throws Exception {
    AbstractMongoNoSpringContextIntegration.setupMongo();
    context = new AnnotationConfigApplicationContext(
        MongoMultiPlayerGameIntegrationConfiguration.class);
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
    gameRepository = context.getBean(AbstractMultiPlayerGameRepository.class);

    gameRepository.deleteAll();
    playerRepository.deleteAll();

    MongoPlayer player = new MongoPlayer();
    player.setSource("M");
    player.setSourceId("2");
    player1 = playerRepository.save(player);

    MongoPlayer player1 = new MongoPlayer();
    player1.setSource("M");
    player1.setSourceId("1");
    player2 = playerRepository.save(player1);
    MongoPlayer player2 = new MongoPlayer();
    player2.setSource("X");
    player2.setSourceId("3");
    player3 = playerRepository.save(player2);
    MongoPlayer player3 = new MongoPlayer();
    player3.setSource("Y");
    player3.setSourceId("2");
    player4 = playerRepository.save(player3);

    cacheManager = context.getBean(CacheManager.class);
    cache = cacheManager.getCache(CacheConstants.GAME_ID_CACHE);
  }

  @Test
  public void testCanCreateGameAndReloadIt() {
    SimpleMultiPlayerGame saved;
    SimpleMultiPlayerGame loaded;
    SimpleMultiPlayerGame save = new SimpleMultiPlayerGame();

    Map<ObjectId, PlayerState> map = new LinkedHashMap<>();
    map.put(player1.getId(), PlayerState.Accepted);
    map.put(player2.getId(), PlayerState.Pending);

    save.setIntValue(5);
    save.setStringValue("X");
    save.setInitiatingPlayer(player1.getId());
    save.setPlayerStates(map);
    save.setPlayers(Arrays.asList(player1, player2));
    save.setFeatures(new HashSet<>(Arrays.asList("Y", "Z")));
    assertNull(save.getId());
    assertNull(save.getCreated());
    assertNull(save.getLastUpdate());
    assertNull(save.getCompletedTimestamp());
    assertNull(save.getDeclinedTimestamp());
    saved = (SimpleMultiPlayerGame) gameRepository.save(save);
    assertNotNull(saved);
    assertNotNull(saved.getId());
    assertNotNull(saved.getLastUpdate());
    assertNotNull(saved.getCreated());
    assertEquals(save.getIntValue(), saved.getIntValue());
    assertEquals(save.getStringValue(), saved.getStringValue());
    assertEquals(save.getPlayers(), saved.getPlayers());
    assertEquals(save.getPlayerStates(), saved.getPlayerStates());
    assertEquals(save.getInitiatingPlayer(), saved.getInitiatingPlayer());
    assertNull(saved.getCompletedTimestamp());
    assertNull(saved.getDeclinedTimestamp());
    assertEquals(save.getFeatures(), saved.getFeatures());

    loaded = (SimpleMultiPlayerGame) gameRepository.findById(saved.getId()).get();
    assertNotNull(loaded);
    assertEquals(saved.getId(), loaded.getId());
    assertEquals(saved.getLastUpdate(), loaded.getLastUpdate());
    assertEquals(save.getCreated(), loaded.getCreated());
    assertEquals(save.getIntValue(), loaded.getIntValue());
    assertEquals(save.getStringValue(), loaded.getStringValue());
    assertEquals(save.getPlayers(), loaded.getPlayers());
    assertEquals(save.getPlayerStates(), loaded.getPlayerStates());
    assertEquals(save.getInitiatingPlayer(), loaded.getInitiatingPlayer());
    assertNull(loaded.getDeclinedTimestamp());
    assertNull(loaded.getCompletedTimestamp());
    assertEquals(save.getFeatures(), loaded.getFeatures());

    assertEquals(1, gameRepository.count());
    assertEquals(1, gameRepository.countByCreatedGreaterThan(start.minusSeconds(1)));
    assertEquals(0, gameRepository.countByCreatedGreaterThan(loaded.getCreated()));
  }

  @Test
  public void testCanUpdateAGame() {
    SimpleMultiPlayerGame update;
    SimpleMultiPlayerGame updated;
    SimpleMultiPlayerGame loaded;
    SimpleMultiPlayerGame initial = new SimpleMultiPlayerGame();

    Map<ObjectId, PlayerState> map = new LinkedHashMap<>();
    map.put(player1.getId(), PlayerState.Accepted);
    map.put(player2.getId(), PlayerState.Pending);

    initial.setIntValue(5);
    initial.setStringValue("X");
    initial.setInitiatingPlayer(player1.getId());
    initial.setPlayers(Arrays.asList(player1, player2));
    initial.setPlayerStates(map);
    initial = (SimpleMultiPlayerGame) gameRepository.save(initial);

    update = (SimpleMultiPlayerGame) gameRepository.findById(initial.getId()).get();
    update.setStringValue(update.getStringValue() + "Z");
    update.getFeatures().add("23");
    update.setCompletedTimestamp(Instant.now());
    update.setIntValue(update.getIntValue() * 2);
    update.setInitiatingPlayer(player2.getId());
    LinkedHashMap<ObjectId, PlayerState> map1 = new LinkedHashMap<>(2);
    map1.put(player1.getId(), PlayerState.Accepted);
    map1.put(player2.getId(), PlayerState.Rejected);
    update.setPlayerStates(map1);
    update.setPlayers(Arrays.asList(player1, player2, player4, player3));
    update.setDeclinedTimestamp(Instant.now());
    updated = (SimpleMultiPlayerGame) gameRepository.save(update);
    assertNotNull(updated);
    assertEquals(initial.getId(), updated.getId());
    assertTrue(updated.getLastUpdate().compareTo(initial.getLastUpdate()) > 0);
    assertEquals(initial.getCreated(), updated.getCreated());
    assertEquals(update.getIntValue(), updated.getIntValue());
    assertEquals(update.getStringValue(), updated.getStringValue());
    assertEquals(update.getPlayers(), updated.getPlayers());
    assertEquals(update.getPlayerStates(), updated.getPlayerStates());
    assertEquals(update.getInitiatingPlayer(), updated.getInitiatingPlayer());
    assertEquals(update.getDeclinedTimestamp(), updated.getDeclinedTimestamp());
    assertEquals(update.getCompletedTimestamp(), updated.getCompletedTimestamp());
    assertEquals(updated.getFeatures(), updated.getFeatures());

    loaded = (SimpleMultiPlayerGame) gameRepository.findById(update.getId()).get();
    assertNotNull(loaded);
    assertEquals(updated.getId(), loaded.getId());
    assertEquals(updated.getLastUpdate(), loaded.getLastUpdate());
    assertEquals(updated.getCreated(), loaded.getCreated());
    assertEquals(updated.getIntValue(), loaded.getIntValue());
    assertEquals(updated.getStringValue(), loaded.getStringValue());
    assertEquals(updated.getPlayers(), loaded.getPlayers());
    assertEquals(updated.getPlayerStates(), loaded.getPlayerStates());
    assertEquals(updated.getInitiatingPlayer(), loaded.getInitiatingPlayer());
    assertEquals(updated.getDeclinedTimestamp(), loaded.getDeclinedTimestamp());
    assertEquals(updated.getCompletedTimestamp(), loaded.getCompletedTimestamp());
    assertEquals(updated.getFeatures(), loaded.getFeatures());

    assertEquals(1, gameRepository.count());
  }

  @Test
  public void testFindGamesByPlayer() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(5);
    game.setStringValue("X");
    game.setPlayers(Arrays.asList(player1, player2));
    game.setGamePhase(GamePhase.Playing);
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);

    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();
    game1.setIntValue(10);
    game1.setStringValue("X");
    game1.setPlayers(Arrays.asList(player1, player3));
    game1.setGamePhase(GamePhase.Playing);
    SimpleMultiPlayerGame p1g2 = (SimpleMultiPlayerGame) gameRepository.save(game1);

    SimpleMultiPlayerGame game2 = new SimpleMultiPlayerGame();
    game2.setIntValue(15);
    game2.setStringValue("2");
    game2.setPlayers(Arrays.asList(player1, player4, player2));
    game2.setGamePhase(GamePhase.Challenged);
    SimpleMultiPlayerGame p1g3 = (SimpleMultiPlayerGame) gameRepository.save(game2);

    SimpleMultiPlayerGame game3 = new SimpleMultiPlayerGame();
    game3.setIntValue(20);
    game3.setStringValue("2");
    game3.setPlayers(Arrays.asList(player2, player4));
    game3.setGamePhase(GamePhase.Challenged);
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(game3);

    List<SimpleMultiPlayerGame> p1g = (List<SimpleMultiPlayerGame>) gameRepository
        .findByPlayersId(player1.getId());
    assertEquals(3, p1g.size());
    assertTrue(p1g.contains(p1g1));
    assertTrue(p1g.contains(p1g2));
    assertTrue(p1g.contains(p1g3));
    List<SimpleMultiPlayerGame> p2g = (List<SimpleMultiPlayerGame>) gameRepository
        .findByPlayersId(player2.getId());
    assertEquals(3, p2g.size());
    assertTrue(p2g.contains(p2g1));
    assertTrue(p2g.contains(p1g1));
    assertTrue(p2g.contains(p1g3));

    assertEquals(4, StreamSupport.stream(gameRepository.findAll().spliterator(), false).count());

    Sort sort = new Sort(Direction.DESC, Arrays.asList("lastUpdate", "created"));
    PageRequest page = PageRequest.of(0, 20, sort);
    List<SimpleMultiPlayerGame> by = (List<SimpleMultiPlayerGame>) gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(player1.getId(), GamePhase.Playing,
            p1g1.getCreated().minusSeconds(24 * 60 * 60), page);
    assertEquals(2, by.size());
    assertTrue(by.contains(p1g1));
    assertTrue(by.contains(p1g2));
  }

  @Test
  public void testSavesCache() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);

    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayers(Arrays.asList(player2, player4));
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(game1);

    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    p1g1.setIntValue(150);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(150, ((SimpleMultiPlayerGame) cache.get(p1g1.getId()).get()).getIntValue());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());
  }

  @Test
  public void testFindsCreatedBefore() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);

    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayers(Arrays.asList(player2, player4));
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(game1);

    List<SimpleMultiPlayerGame> games;
    games = (List<SimpleMultiPlayerGame>) gameRepository.findByCreatedLessThan(p1g1.getCreated());
    Assert.assertFalse(games.contains(p1g1));
    Assert.assertFalse(games.contains(p2g1));

    games = (List<SimpleMultiPlayerGame>) gameRepository.findByCreatedLessThan(Instant.now());
    assertTrue(games.contains(p1g1));
    assertTrue(games.contains(p2g1));
  }

  @Test
  public void testDeleteCreatedBefore() throws InterruptedException {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);

    Thread.sleep(100);

    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayers(Arrays.asList(player2, player4));
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(game1);

    assertTrue(gameRepository.findById(p1g1.getId()).isPresent());
    assertTrue(gameRepository.findById(p2g1.getId()).isPresent());

    assertTrue(1 <= gameRepository.deleteByCreatedLessThan(p2g1.getCreated()));

    Assert.assertFalse(gameRepository.findById(p1g1.getId()).isPresent());
    assertTrue(gameRepository.findById(p2g1.getId()).isPresent());
  }

  @Test
  public void testDeleteAllCache() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);

    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayers(Arrays.asList(player2, player4));
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(game1);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());

    gameRepository.deleteAll();

    assertNull(cache.get(p1g1.getId()));
    assertNull(cache.get(p2g1.getId()));
  }

  @Test
  public void testSingleDeletesForCache() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);
    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();

    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayers(Arrays.asList(player2, player4));
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(game1);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());

    gameRepository.delete(p1g1);
    gameRepository.deleteById(p2g1.getId());

    assertNull(cache.get(p1g1.getId()));
    assertNull(cache.get(p2g1.getId()));
  }

  @Test
  public void testIterableDeletesForCache() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);
    SimpleMultiPlayerGame game1 = new SimpleMultiPlayerGame();
    game1.setIntValue(20);
    game1.setStringValue("2");
    game1.setPlayers(Arrays.asList(player2, player4));
    SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository
        .save(game1);
    gameRepository.saveAll(new ArrayList<>(Arrays.asList(p1g1, p2g1)));
    assertEquals(p1g1, cache.get(p1g1.getId()).get());
    assertEquals(p2g1, cache.get(p2g1.getId()).get());

    gameRepository.deleteAll(Arrays.asList(p1g1, p2g1));

    assertNull(cache.get(p1g1.getId()));
    assertNull(cache.get(p2g1.getId()));
  }

  @Test
  public void testCacheReallyHit() {
    SimpleMultiPlayerGame game = new SimpleMultiPlayerGame();
    game.setIntValue(15);
    game.setStringValue("2");
    game.setPlayers(Arrays.asList(player1, player4, player2));
    SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(game);
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
    assertEquals(4L, playerRepository.count());
  }

  @Configuration
  @EnableMongoRepositories(basePackages = {"com.jtbdevelopment"}, excludeFilters = {
      @Filter(type = FilterType.REGEX, pattern = {
          "com.jtbdevelopment.games.mongo.state.utility.SimpleSinglePlayerGameRepository"})})
  @EnableMongoAuditing
  @ComponentScan(basePackages = {"com.jtbdevelopment"}, excludeFilters = {
      @Filter(type = FilterType.REGEX, pattern = {
          "com.jtbdevelopment.core.mongo.spring.social.dao.*IntegrationSocialConfiguration",
          "com.jtbdevelopment.*.*MongoSinglePlayerGameIntegrationConfiguration",
          "com.jtbdevelopment.*.*MongoPlayerIntegrationConfiguration",
          "com.jtbdevelopment.*.*CoreSpringConfiguration",
          "com.jtbdevelopment.*.*MongoConfiguration"})})
  public static class MongoMultiPlayerGameIntegrationConfiguration extends
      AbstractCoreMongoConfiguration {

    public MongoMultiPlayerGameIntegrationConfiguration(final List<MongoConverter> mongoConverters,
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
