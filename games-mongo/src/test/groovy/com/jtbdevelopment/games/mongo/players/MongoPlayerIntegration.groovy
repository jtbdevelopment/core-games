package com.jtbdevelopment.games.mongo.players

import com.fasterxml.jackson.databind.ObjectMapper
import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration
import com.jtbdevelopment.core.mongo.spring.AbstractMongoNoSpringContextIntegration
import com.jtbdevelopment.games.dao.caching.CacheConstants
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import com.jtbdevelopment.games.players.notifications.RegisteredDevice
import com.mongodb.DBCollection
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.social.connect.support.ConnectionFactoryRegistry

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/11/15
 * Time: 3:09 PM
 */
@CompileStatic
class MongoPlayerIntegration extends AbstractMongoNoSpringContextIntegration {
    @Configuration
    @EnableMongoRepositories(
            basePackages = ["com.jtbdevelopment"],
            excludeFilters = [
                    @ComponentScan.Filter(
                            type = FilterType.REGEX,
                            pattern = ["com.jtbdevelopment.games.mongo.state.utility.SimpleMultiPlayerGameRepository"]
                    ),
            ]
    )

    @EnableMongoAuditing
    @ComponentScan(
            basePackages = ['com.jtbdevelopment'],
            excludeFilters = [
                    @ComponentScan.Filter(type = FilterType.REGEX, pattern = [
                            "com.jtbdevelopment.core.mongo.spring.social.dao.*IntegrationSocialConfiguration",
                            "com.jtbdevelopment.*.*MongoMultiPlayerGameIntegrationConfiguration",
                            "com.jtbdevelopment.*.*MongoSinglePlayerGameIntegrationConfiguration",
                            "com.jtbdevelopment.*.*CoreSpringConfiguration",
                            "com.jtbdevelopment.*.*MongoConfiguration"
                    ])
            ]
    )
    static class MongoPlayerIntegrationConfiguration extends AbstractCoreMongoConfiguration {
        @Bean
        @Autowired
        ConnectionFactoryRegistry connectionFactoryLocator() {
            ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry()
            return registry
        }

        @Override
        protected String getMappingBasePackage() {
            Package mappingBasePackage = getClass().getPackage();
            return mappingBasePackage == null ? null : mappingBasePackage.getName();
        }
    }

    private static final String PLAYER_COLLECTION_NAME = 'player'
    private DBCollection collection

    MongoPlayerRepository playerRepository
    MongoPlayer player1, player2, player3, player4, number4
    MongoManualPlayer manualPlayer
    MongoSystemPlayer systemPlayer
    CacheManager cacheManager
    static ApplicationContext context

    protected MongoPlayer makeSimplePlayer(final String id, final boolean disabled = false) {
        return (MongoPlayer) playerRepository.save(new MongoPlayer(
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                lastVersionNotes: 'X.Y',
                lastLogin: ZonedDateTime.now(ZoneId.of("GMT")),
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id))
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @BeforeClass
    static void setupAll() {
        setupMongo()
        context = new AnnotationConfigApplicationContext(MongoPlayerIntegrationConfiguration.class)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @AfterClass
    static void tearDownAll() {
        tearDownMongo()
    }

    @Before
    void setup() {
        assert db.collectionExists(PLAYER_COLLECTION_NAME)
        collection = db.getCollection(PLAYER_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        playerRepository.deleteAll()

        player1 = makeSimplePlayer('1')
        player1.payLevel = PlayerPayLevel.PremiumPlayer
        player2 = makeSimplePlayer('2')
        player3 = makeSimplePlayer('3', true)
        player4 = makeSimplePlayer('4')
        number4 = makeSimplePlayer('4IAmNumber')
        manualPlayer = (MongoManualPlayer) playerRepository.save(new MongoManualPlayer(
                sourceId: "MADEUP" + "M",
                displayName: "M",
                disabled: false))
        systemPlayer = (MongoSystemPlayer) playerRepository.save(new MongoSystemPlayer(
                sourceId: "MADEUP" + "S",
                displayName: "S",
                disabled: false))
        cacheManager = context.getBean(CacheManager.class)
    }

    @Test
    void testFindByDisplayName() {
        assert null == playerRepository.findByDisplayName('Humpty Dumpty')
        assert player1 == playerRepository.findByDisplayName(player1.displayName)
    }

    @Test
    void testFindByDisplayNameContainsPageable() {
        def page = new PageRequest(
                1,
                1,
                Sort.Direction.ASC,
                'displayName')
        assert 0L == playerRepository.findByDisplayNameContains('Humpty Dumpty', page).getTotalElements()
        Page<Player<ObjectId>> contains = playerRepository.findByDisplayNameContains('4', page)
        assert 2L == contains.getTotalElements()
        assert 1 == contains.getNumberOfElements()
        assert number4 == ++contains.iterator()

        page = new PageRequest(
                0,
                3,
                Sort.Direction.ASC,
                'displayName')
        contains = playerRepository.findByDisplayNameContains('', page)
        assert 7 == contains.getTotalElements()
        assert 3 == contains.getNumberOfElements()
        assert player1 == ++contains.iterator()
    }

    @Test
    void testFindBySourceAndDisabled() {
        assert playerRepository.findBySourceAndDisabled(systemPlayer.source, true).isEmpty()
        assert playerRepository.findBySourceAndDisabled(systemPlayer.source, false) == [systemPlayer] as List<Player>
        assert playerRepository.findBySourceAndDisabled(manualPlayer.source, true).isEmpty()
        assert playerRepository.findBySourceAndDisabled(manualPlayer.source, false) == [manualPlayer] as List<Player>
        assert playerRepository.findBySourceAndDisabled(player1.source, false) as Set == [player1, player2, player4, number4] as Set
        assert playerRepository.findBySourceAndDisabled(player1.source, true) == [player3] as List<Player>
    }

    @Test
    void testFindBySourceAndSourceIds() {
        List<Player> players = playerRepository.findBySourceAndSourceIdIn(player1.source, [player1.sourceId, player2.sourceId, 'X'])
        assert players.size() == 2
        assert players.contains(player1)
        assert players.contains(player2)
    }

    @Test
    void testFindBySourceAndSourceId() {
        assert playerRepository.findBySourceAndSourceId(player4.source, player4.sourceId) == player4
        assert playerRepository.findBySourceAndSourceId(systemPlayer.source, systemPlayer.sourceId) == systemPlayer
        assert playerRepository.findBySourceAndSourceId(manualPlayer.source, manualPlayer.sourceId) == manualPlayer
        assert playerRepository.findBySourceAndSourceId(manualPlayer.source, manualPlayer.sourceId + 'X') == null
    }

    @Test
    void testFindByMD5() {
        Player player = playerRepository.findByMd5(player1.md5)
        assert player == player1
    }

    @Test
    void testFindByMD5s() {
        List<Player> players = playerRepository.findByMd5In([player1.md5, player3.md5, systemPlayer.md5])
        assert players.size() == 3
        assert players.contains(player1)
        assert players.contains(player3)
        assert players.contains(systemPlayer)
    }

    @Test
    void testSerialization() {
        ObjectMapper mapper = context.getBean(ObjectMapper.class)

        ZonedDateTime ll = player1.lastLogin
        ZonedDateTime c = player1.created
        try {
            player1.lastLogin = ZonedDateTime.of(2015, 11, 10, 1, 2, 3, 100, ZoneId.of("GMT"))
            player1.created = ZonedDateTime.of(200, 1, 30, 4, 5, 6, 100, ZoneId.of("GMT"))
            player1.updateRegisteredDevice(new RegisteredDevice(deviceID: "ADevice", lastRegistered: player1.lastLogin))

            assert mapper.writeValueAsString(player1) == '{"source":"MADEUP","sourceId":"MADEUP1","displayName":"1","imageUrl":"http://somewhere.com/image/1","profileUrl":"http://somewhere.com/profile/1","registeredDevices":[{"deviceID":"ADevice","lastRegistered":1447117323.000000100}],"created":-55853265294.000000100,"lastLogin":1447117323.000000100,"lastVersionNotes":"X.Y","disabled":false,"adminUser":false,"payLevel":"PremiumPlayer","gameSpecificPlayerAttributes":null,"id":"' + player1.idAsString + '","md5":"' + player1.md5 + '"}'
        } finally {
            player1.lastLogin = ll
            player1.created = c
        }
    }

    @Test
    void testDeserialization() {
        ObjectMapper mapper = context.getBean(ObjectMapper.class)
        MongoPlayer player = mapper.readValue(
                '{"registeredDevices":[{"deviceID":"ADevice","lastRegistered":1447117323.000000100}],"source":"MADEUP","sourceId":"MADEUP1","displayName":"1","imageUrl":"http://somewhere.com/image/1","profileUrl":"http://somewhere.com/profile/1","disabled":false,"adminUser":true,"id":"54b656dba826d455d3eaa8a4","md5":"94026ad238c04d23e4fd1fe7efeebabf", "payLevel": "PremiumPlayer"}',
                MongoPlayer.class
        )
        assert player
        assert player.idAsString == '54b656dba826d455d3eaa8a4'
        assert player.md5 == '94026ad238c04d23e4fd1fe7efeebabf'
        assert player.payLevel == PlayerPayLevel.PremiumPlayer
        assert player.adminUser
        assert player.profileUrl == "http://somewhere.com/profile/1"
        assert player.imageUrl == "http://somewhere.com/image/1"
        assert !player.registeredDevices.empty
        RegisteredDevice device = player.registeredDevices.iterator().next()
        assert "ADevice" == device.deviceID
        assert ZonedDateTime.of(2015, 11, 10, 1, 2, 3, 100, ZoneId.of("UTC")) == device.lastRegistered
    }

    @Test
    void testCacheLookupFromInitialSaves() {
        def cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id).get() == player1
        assert cache.get(player2.id).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player1.md5).get() == player1
        assert cache.get(player2.md5).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1
        assert cache.get(player2.source + "/" + player2.sourceId).get() == player2
        assert cache.get('JUNK') == null
    }

    @Test
    void testCacheLookupFromBulkSaves() {
        def cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        cache.clear()
        assert cache.get(player1.id) == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        cache.clear()
        assert cache.get(player1.md5) == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        cache.clear()
        assert cache.get(player1.source + "/" + player1.sourceId) == null

        playerRepository.save([player1, player2] as List<Player>)

        cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id).get() == player1
        assert cache.get(player2.id).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player1.md5).get() == player1
        assert cache.get(player2.md5).get() == player2
        assert cache.get('JUNK') == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1
        assert cache.get(player2.source + "/" + player2.sourceId).get() == player2
        assert cache.get('JUNK') == null
    }

    @Test
    void testCacheDeleteAll() {
        def cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id).get() == player1
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player1.md5).get() == player1
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1

        playerRepository.deleteAll()

        cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id) == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player2.md5) == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId) == null
    }

    @Test
    void testCacheDeleteSingly() {
        def cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id).get() == player1
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player2.md5).get() == player2
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1

        playerRepository.delete(player1.id)
        playerRepository.delete(player2)

        cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id) == null
        assert cache.get(player3.id).get() == player3
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player2.md5) == null
        assert cache.get(player3.md5).get() == player3
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId) == null
        assert cache.get(player3.source + "/" + player3.sourceId).get() == player3
    }

    @Test
    void testCacheDeleteMulti() {
        def cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id).get() == player1
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player2.md5).get() == player2
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId).get() == player1

        playerRepository.delete([player2, player1] as List<Player>)

        cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        assert cache.get(player1.id) == null
        assert cache.get(player3.id).get() == player3
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        assert cache.get(player2.md5) == null
        assert cache.get(player3.md5).get() == player3
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        assert cache.get(player1.source + "/" + player1.sourceId) == null
        assert cache.get(player3.source + "/" + player3.sourceId).get() == player3
    }

    @Test
    void testFindCaching() {
        //  Delete data underneath the cache to show still being pulled from caches
        MongoOperations operations = context.getBean(MongoOperations)
        operations.remove(Query.query(Criteria.where('source').is(player1.source)), 'player')

        assert playerRepository.findOne(player1.id) == player1
        assert playerRepository.findBySourceAndSourceId(player2.source, player2.sourceId) == player2
        assert playerRepository.findByMd5In([player3.md5, player4.md5]) as Set == [player3, player4] as Set
        assert playerRepository.findBySourceAndSourceIdIn(player2.source, [player2.sourceId, player1.sourceId]) as Set == [player2, player1] as Set

        //  Not cached
        assert playerRepository.findBySourceAndDisabled(player2.source, false).isEmpty()

        def cache = cacheManager.getCache(CacheConstants.PLAYER_ID_CACHE)
        cache.clear()
        assert playerRepository.findOne(player1.id) == null
        cache = cacheManager.getCache(CacheConstants.PLAYER_S_AND_SID_CACHE)
        cache.clear()
        assert playerRepository.findBySourceAndSourceId(player2.source, player2.sourceId) == null
        assert playerRepository.findBySourceAndSourceIdIn(player2.source, [player2.sourceId, player1.sourceId]).isEmpty()
        cache = cacheManager.getCache(CacheConstants.PLAYER_MD5_CACHE)
        cache.clear()
        assert playerRepository.findByMd5In([player3.md5, player4.md5]).isEmpty()
    }

    @Test
    void testFindByLastLogin() {
        List<Player> playerList = playerRepository.findByLastLoginLessThan(player1.created.minusMinutes(1))
        assert 2 == playerList.size()
        playerList = playerRepository.findByLastLoginLessThan(systemPlayer.created.plusHours(1))
        assert 7 == playerList.size()
    }

    @Test
    void testDeleteByLastLogin() {
        Player p = makeSimplePlayer('DELETEME')
        ZonedDateTime oldDate = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("GMT"))
        assert ((long) 0) == playerRepository.deleteByLastLoginLessThan(oldDate)
        p.lastLogin = oldDate.minusMinutes(1)
        playerRepository.save(p)

        assert ((long) 1) == playerRepository.deleteByLastLoginLessThan(oldDate)
        assert null == playerRepository.findOne((ObjectId) p.id)
    }
}


