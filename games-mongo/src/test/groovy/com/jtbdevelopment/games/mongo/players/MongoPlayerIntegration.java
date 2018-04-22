package com.jtbdevelopment.games.mongo.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration;
import com.jtbdevelopment.core.mongo.spring.AbstractMongoNoSpringContextIntegration;
import com.jtbdevelopment.core.mongo.spring.MongoProperties;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import com.jtbdevelopment.games.dao.caching.CacheConstants;
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerPayLevel;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.mongodb.client.MongoCollection;
import groovy.transform.CompileStatic;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

/**
 * Date: 1/11/15 Time: 3:09 PM
 */
@SuppressWarnings("ConstantConditions")
@CompileStatic
public class MongoPlayerIntegration extends AbstractMongoNoSpringContextIntegration {

  private static final String PLAYER_COLLECTION_NAME = "player";
  private static ApplicationContext context;
  private MongoCollection collection;
  private MongoPlayerRepository playerRepository;
  private MongoPlayer player1;
  private MongoPlayer player2;
  private MongoPlayer player3;
  private MongoPlayer player4;
  private MongoPlayer number4;
  private MongoManualPlayer manualPlayer;
  private MongoSystemPlayer systemPlayer;
  private CacheManager cacheManager;

  @SuppressWarnings("GroovyUnusedDeclaration")
  @BeforeClass
  public static void setupAll() throws Exception {
    AbstractMongoNoSpringContextIntegration.setupMongo();
    context = new AnnotationConfigApplicationContext(MongoPlayerIntegrationConfiguration.class);
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  @AfterClass
  public static void tearDownAll() throws Exception {
    AbstractMongoNoSpringContextIntegration.tearDownMongo();
  }

  private MongoPlayer makeSimplePlayer(final String id, final boolean disabled) {
    MongoPlayer player = new MongoPlayer();
    player.setSource("MADEUP");
    player.setSourceId("MADEUP" + id);
    player.setDisplayName(id);
    player.setDisabled(disabled);
    player.setLastVersionNotes("X.Y");
    player.setLastLogin(Instant.now());
    player.setImageUrl("http://somewhere.com/image/" + id);
    player.setProfileUrl("http://somewhere.com/profile/" + id);
    return playerRepository.save(player);
  }

  private MongoPlayer makeSimplePlayer(final String id) {
    return makeSimplePlayer(id, false);
  }

  @Before
  public void setup() {
    collection = db.getCollection(PLAYER_COLLECTION_NAME);

    playerRepository = context.getBean(MongoPlayerRepository.class);
    playerRepository.deleteAll();

    player1 = makeSimplePlayer("1");
    player1.setPayLevel(PlayerPayLevel.PremiumPlayer);
    player2 = makeSimplePlayer("2");
    player3 = makeSimplePlayer("3", true);
    player4 = makeSimplePlayer("4");
    number4 = makeSimplePlayer("4IAmNumber");
    MongoManualPlayer player = new MongoManualPlayer();
    player.setSourceId("MADEUP" + "M");
    player.setDisplayName("M");
    player.setDisabled(false);
    manualPlayer = playerRepository.save(player);
    MongoSystemPlayer player1 = new MongoSystemPlayer();
    player1.setSourceId("MADEUP" + "S");
    player1.setDisplayName("S");
    player1.setDisabled(false);
    systemPlayer = playerRepository.save(player1);
    cacheManager = context.getBean(CacheManager.class);
  }

  @Test
  public void testFindByDisplayName() {
    Assert.assertNull(playerRepository.findByDisplayName("Humpty Dumpty"));
    assertEquals(player1, playerRepository.findByDisplayName(player1.getDisplayName()));
  }

  @Test
  public void testFindByDisplayNameContainsPageable() {
    PageRequest page = PageRequest.of(1, 1, Direction.ASC, "displayName");
    assertEquals(0L,
        playerRepository.findByDisplayNameContains("Humpty Dumpty", page).getTotalElements());
    Page<MongoPlayer> contains = playerRepository.findByDisplayNameContains("4", page);
    assertEquals(2L, contains.getTotalElements());
    assertEquals(1, contains.getNumberOfElements());
    assertEquals(number4, contains.iterator().next());

    page = PageRequest.of(0, 3, Direction.ASC, "displayName");
    contains = playerRepository.findByDisplayNameContains("", page);
    assertEquals(7L, contains.getTotalElements());
    assertEquals(3, contains.getNumberOfElements());
    assertEquals(player1, contains.iterator().next());
  }

  @Test
  public void testFindBySourceAndDisabled() {
    assertTrue(
        playerRepository.findBySourceAndDisabled(systemPlayer.getSource(), true).isEmpty());
    assertEquals(Collections.singletonList(systemPlayer),
        playerRepository.findBySourceAndDisabled(systemPlayer.getSource(), false));
    assertTrue(
        playerRepository.findBySourceAndDisabled(manualPlayer.getSource(), true).isEmpty());
    assertEquals(Collections.singletonList(manualPlayer),
        playerRepository.findBySourceAndDisabled(manualPlayer.getSource(), false));
    assertEquals(Arrays.asList(player1, player2, player4, number4),
        playerRepository.findBySourceAndDisabled(player1.getSource(), false));
    assertEquals(Collections.singletonList(player3),
        playerRepository.findBySourceAndDisabled(player1.getSource(), true));
  }

  @Test
  public void testFindBySourceAndSourceIds() {
    List<MongoPlayer> players = playerRepository.findBySourceAndSourceIdIn(player1.getSource(),
        Arrays.asList(player1.getSourceId(), player2.getSourceId(), "X"));
    assertEquals(2, players.size());
    assertTrue(players.contains(player1));
    assertTrue(players.contains(player2));
  }

  @Test
  public void testFindBySourceAndSourceId() {
    assertEquals(player4,
        playerRepository.findBySourceAndSourceId(player4.getSource(), player4.getSourceId()));
    assertEquals(systemPlayer, playerRepository
        .findBySourceAndSourceId(systemPlayer.getSource(), systemPlayer.getSourceId()));
    assertEquals(manualPlayer, playerRepository
        .findBySourceAndSourceId(manualPlayer.getSource(), manualPlayer.getSourceId()));
    Assert.assertNull(playerRepository
        .findBySourceAndSourceId(manualPlayer.getSource(), manualPlayer.getSourceId() + "X"));
  }

  @Test
  public void testFindByMD5() {
    Player player = playerRepository.findByMd5(player1.getMd5());
    assertEquals(player1, player);
  }

  @Test
  public void testFindByMD5s() {
    List<MongoPlayer> players = playerRepository
        .findByMd5In(Arrays.asList(player1.getMd5(), player3.getMd5(), systemPlayer.getMd5()));
    assertEquals(3, players.size());
    assertTrue(players.contains(player1));
    assertTrue(players.contains(player3));
    assertTrue(players.contains(systemPlayer));
  }

  @Test
  public void testSerialization() throws JsonProcessingException {
    ObjectMapper mapper = context.getBean(ObjectMapper.class);

    Instant ll = player1.getLastLogin();
    Instant c = player1.getCreated();
    try {
      player1
          .setLastLogin(ZonedDateTime.of(2015, 11, 10, 1, 2, 3, 100, ZoneId.of("GMT")).toInstant());
      player1.setCreated(ZonedDateTime.of(200, 1, 30, 4, 5, 6, 100, ZoneId.of("GMT")).toInstant());
      RegisteredDevice device = new RegisteredDevice();
      device.setDeviceID("ADevice");
      device.setLastRegistered(player1.getLastLogin());
      player1.updateRegisteredDevice(device);

      assertEquals(
          "{\"source\":\"MADEUP\",\"sourceId\":\"MADEUP1\",\"displayName\":\"1\",\"imageUrl\":\"http://somewhere.com/image/1\",\"profileUrl\":\"http://somewhere.com/profile/1\",\"registeredDevices\":[{\"deviceID\":\"ADevice\",\"lastRegistered\":1447117323.000000100}],\"created\":-55853265294.000000100,\"lastLogin\":1447117323.000000100,\"lastVersionNotes\":\"X.Y\",\"disabled\":false,\"adminUser\":false,\"payLevel\":\"PremiumPlayer\",\"gameSpecificPlayerAttributes\":null,\"id\":\""
              + player1.getIdAsString() + "\",\"md5\":\"" + player1.getMd5() + "\"}",
          mapper.writeValueAsString(player1));
    } finally {
      player1.setLastLogin(ll);
      player1.setCreated(c);
    }

  }

  @Test
  public void testDeserialization() throws IOException {
    ObjectMapper mapper = context.getBean(ObjectMapper.class);
    MongoPlayer player = mapper.readValue(
        "{\"registeredDevices\":[{\"deviceID\":\"ADevice\",\"lastRegistered\":1447117323.000000100}],\"source\":\"MADEUP\",\"sourceId\":\"MADEUP1\",\"displayName\":\"1\",\"imageUrl\":\"http://somewhere.com/image/1\",\"profileUrl\":\"http://somewhere.com/profile/1\",\"disabled\":false,\"adminUser\":true,\"id\":\"54b656dba826d455d3eaa8a4\",\"md5\":\"94026ad238c04d23e4fd1fe7efeebabf\", \"payLevel\": \"PremiumPlayer\"}",
        MongoPlayer.class);
    Assert.assertNotNull(player);
    assertEquals("54b656dba826d455d3eaa8a4", player.getIdAsString());
    assertEquals("94026ad238c04d23e4fd1fe7efeebabf", player.getMd5());
    assertEquals(PlayerPayLevel.PremiumPlayer, player.getPayLevel());
    assertTrue(player.getAdminUser());
    assertEquals("http://somewhere.com/profile/1", player.getProfileUrl());
    assertEquals("http://somewhere.com/image/1", player.getImageUrl());
    Assert.assertFalse(player.getRegisteredDevices().isEmpty());
    RegisteredDevice device = player.getRegisteredDevices().iterator().next();
    assertEquals("ADevice", device.getDeviceID());
    assertEquals(ZonedDateTime.of(2015, 11, 10, 1, 2, 3, 100, ZoneId.of("UTC")).toInstant(),
        device.getLastRegistered());
  }

  @Test
  public void testCacheLookupFromInitialSaves() {
    Cache cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    assertEquals(player1, cache.get(player1.getId()).get());
    assertEquals(player2, cache.get(player2.getId()).get());
    Assert.assertNull(cache.get("JUNK"));
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    assertEquals(player1, cache.get(player1.getMd5()).get());
    assertEquals(player2, cache.get(player2.getMd5()).get());
    Assert.assertNull(cache.get("JUNK"));
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    assertEquals(player1, cache.get(player1.getSource() + "/" + player1.getSourceId()).get());
    assertEquals(player2, cache.get(player2.getSource() + "/" + player2.getSourceId()).get());
    Assert.assertNull(cache.get("JUNK"));
  }

  @Test
  public void testCacheLookupFromBulkSaves() {
    Cache cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    cache.clear();
    Assert.assertNull(cache.get(player1.getId()));
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    cache.clear();
    Assert.assertNull(cache.get(player1.getMd5()));
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    cache.clear();
    Assert.assertNull(cache.get(player1.getSource() + "/" + player1.getSourceId()));

    playerRepository.saveAll(Arrays.asList(player1, player2));

    cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    assertEquals(player1, cache.get(player1.getId()).get());
    assertEquals(player2, cache.get(player2.getId()).get());
    Assert.assertNull(cache.get("JUNK"));
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    assertEquals(player1, cache.get(player1.getMd5()).get());
    assertEquals(player2, cache.get(player2.getMd5()).get());
    Assert.assertNull(cache.get("JUNK"));
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    assertEquals(player1, cache.get(player1.getSource() + "/" + player1.getSourceId()).get());
    assertEquals(player2, cache.get(player2.getSource() + "/" + player2.getSourceId()).get());
    Assert.assertNull(cache.get("JUNK"));
  }

  @Test
  public void testCacheDeleteAll() {
    Cache cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    assertEquals(player1, cache.get(player1.getId()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    assertEquals(player1, cache.get(player1.getMd5()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    assertEquals(player1, cache.get(player1.getSource() + "/" + player1.getSourceId()).get());

    playerRepository.deleteAll();

    cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    Assert.assertNull(cache.get(player1.getId()));
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    Assert.assertNull(cache.get(player2.getMd5()));
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    Assert.assertNull(cache.get(player1.getSource() + "/" + player1.getSourceId()));
  }

  @Test
  public void testCacheDeleteSingly() {
    Cache cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    assertEquals(player1, cache.get(player1.getId()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    assertEquals(player2, cache.get(player2.getMd5()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    assertEquals(player1, cache.get(player1.getSource() + "/" + player1.getSourceId()).get());

    playerRepository.deleteById(player1.getId());
    playerRepository.delete(player2);

    cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    Assert.assertNull(cache.get(player1.getId()));
    assertEquals(player3, cache.get(player3.getId()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    Assert.assertNull(cache.get(player2.getMd5()));
    assertEquals(player3, cache.get(player3.getMd5()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    Assert.assertNull(cache.get(player1.getSource() + "/" + player1.getSourceId()));
    assertEquals(player3, cache.get(player3.getSource() + "/" + player3.getSourceId()).get());
  }

  @Test
  public void testCacheDeleteMulti() {
    Cache cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    assertEquals(player1, cache.get(player1.getId()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    assertEquals(player2, cache.get(player2.getMd5()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    assertEquals(player1, cache.get(player1.getSource() + "/" + player1.getSourceId()).get());

    playerRepository.deleteAll(Arrays.asList(player2, player1));

    cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    Assert.assertNull(cache.get(player1.getId()));
    assertEquals(player3, cache.get(player3.getId()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    Assert.assertNull(cache.get(player2.getMd5()));
    assertEquals(player3, cache.get(player3.getMd5()).get());
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    Assert.assertNull(cache.get(player1.getSource() + "/" + player1.getSourceId()));
    assertEquals(player3, cache.get(player3.getSource() + "/" + player3.getSourceId()).get());
  }

  @Test
  public void testFindCaching() {
    //  Delete data underneath the cache to show still being pulled from caches
    MongoOperations operations = context.getBean(MongoOperations.class);
    operations.remove(Query.query(Criteria.where("source").is(player1.getSource())), "player");

    assertEquals(player1, playerRepository.findById(player1.getId()).get());
    assertEquals(player2,
        playerRepository.findBySourceAndSourceId(player2.getSource(), player2.getSourceId()));
    assertEquals(Arrays.asList(player3, player4),
        playerRepository
            .findByMd5In(new ArrayList<>(Arrays.asList(player3.getMd5(), player4.getMd5()))));
    assertEquals(Arrays.asList(player2, player1),
        playerRepository.findBySourceAndSourceIdIn(player2.getSource(),
            Arrays.asList(player2.getSourceId(), player1.getSourceId())));

    //  Not cached
    assertTrue(playerRepository.findBySourceAndDisabled(player2.getSource(), false).isEmpty());

    Cache cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE);
    cache.clear();
    Assert.assertFalse(playerRepository.findById(player1.getId()).isPresent());
    cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE);
    cache.clear();
    Assert.assertNull(
        playerRepository.findBySourceAndSourceId(player2.getSource(), player2.getSourceId()));
    assertTrue(playerRepository.findBySourceAndSourceIdIn(player2.getSource(),
        new ArrayList<>(Arrays.asList(player2.getSourceId(), player1.getSourceId())))
        .isEmpty());
    cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE);
    cache.clear();
    assertTrue(playerRepository
        .findByMd5In(new ArrayList<>(Arrays.asList(player3.getMd5(), player4.getMd5())))
        .isEmpty());
  }

  @Test
  public void testFindByLastLogin() {
    List<MongoPlayer> playerList = playerRepository
        .findByLastLoginLessThan(player1.getCreated().minusSeconds(60));
    assertEquals(2, playerList.size());
    playerList = playerRepository
        .findByLastLoginLessThan(systemPlayer.getCreated().plusSeconds(60 * 60));
    assertEquals(7, playerList.size());
  }

  @Test
  public void testDeleteByLastLogin() {
    MongoPlayer p = makeSimplePlayer("DELETEME");
    Instant oldDate = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("GMT")).toInstant();
    assertEquals(0L, playerRepository.deleteByLastLoginLessThan(oldDate));
    p.setLastLogin(oldDate.minusSeconds(60));
    playerRepository.save(p);

    assertEquals(1L, playerRepository.deleteByLastLoginLessThan(oldDate));
    Assert.assertFalse(playerRepository.findById(p.getId()).isPresent());
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
          "com.jtbdevelopment.*.*MongoSinglePlayerGameIntegrationConfiguration",
          "com.jtbdevelopment.*.*CoreSpringConfiguration",
          "com.jtbdevelopment.*.*MongoConfiguration"})})
  public static class MongoPlayerIntegrationConfiguration extends AbstractCoreMongoConfiguration {

    public MongoPlayerIntegrationConfiguration(final List<MongoConverter> mongoConverters,
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
