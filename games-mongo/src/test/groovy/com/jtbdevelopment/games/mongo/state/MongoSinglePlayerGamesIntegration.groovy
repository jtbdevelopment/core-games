package com.jtbdevelopment.games.mongo.state

import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration
import com.jtbdevelopment.core.mongo.spring.AbstractMongoNoSpringContextIntegration
import com.jtbdevelopment.core.mongo.spring.MongoProperties
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.dao.caching.CacheConstants
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.mongo.state.utility.SimpleSinglePlayerGame
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.GamePhase
import com.mongodb.client.MongoCollection
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.social.connect.support.ConnectionFactoryRegistry

import java.time.Instant
import java.util.stream.StreamSupport

import static org.junit.Assert.*

/**
 * Date: 1/10/15
 * Time: 2:35 PM
 */
class MongoSinglePlayerGamesIntegration extends AbstractMongoNoSpringContextIntegration {
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
                            "com.jtbdevelopment.*.*MongoPlayerIntegrationConfiguration",
                            "com.jtbdevelopment.*.*CoreSpringConfiguration",
                            "com.jtbdevelopment.*.*MongoConfiguration"
                    ])
            ]
    )
    static class MongoSinglePlayerGameIntegrationConfiguration extends AbstractCoreMongoConfiguration {
        MongoSinglePlayerGameIntegrationConfiguration(
                final List<MongoConverter> mongoConverters,
                final MongoProperties mongoProperties) {
            super(mongoConverters, mongoProperties)
        }

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

    private static final String GAMES_COLLECTION_NAME = 'single'
    private MongoCollection collection

    private MongoPlayerRepository playerRepository
    private AbstractSinglePlayerGameRepository gameRepository
    private MongoPlayer player1, player2
    private CacheManager cacheManager
    private Cache cache
    private Instant start
    private static ApplicationContext context

    @BeforeClass
    static void setupAll() {
        setupMongo()
        context = new AnnotationConfigApplicationContext(MongoSinglePlayerGameIntegrationConfiguration.class)
    }

    @AfterClass
    static void tearDownAll() {
        tearDownMongo()
    }

    @Before
    void setup() {
        start = Instant.now()
        collection = db.getCollection(GAMES_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        gameRepository = context.getBean(AbstractSinglePlayerGameRepository.class)

        gameRepository.deleteAll()
        playerRepository.deleteAll()

        player1 = (MongoPlayer) playerRepository.save(new MongoPlayer(source: "MANUAL", sourceId: "MAN1"))
        player2 = (MongoPlayer) playerRepository.save(new MongoPlayer(source: "MANUAL", sourceId: "MAN2"))

        cacheManager = context.getBean(CacheManager.class)
        cache = cacheManager.getCache(CacheConstants.GAME_ID_CACHE)
    }

    @Test
    void testCanCreateGameAndReloadIt() {
        SimpleSinglePlayerGame save, saved, loaded
        save = new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1, featureData: ['H': 'LON'] as Map<String, Object>, features: ['GG', 'A'] as Set)
        assertNull save.id
        assertNull save.created
        assertNull save.lastUpdate
        assertNull save.completedTimestamp
        saved = (SimpleSinglePlayerGame) gameRepository.save(save)
        assertNotNull saved
        assertNotNull saved.id
        assertNotNull saved.lastUpdate
        assertNotNull saved.created
        assertEquals save.intValue, saved.intValue
        assertEquals save.stringValue, saved.stringValue
        assertEquals save.player, saved.player
        assertNull saved.completedTimestamp
        assertEquals save.featureData, saved.featureData
        assertEquals save.features, saved.features

        loaded = (SimpleSinglePlayerGame) gameRepository.findById(saved.id).get()
        assertNotNull loaded
        assertEquals saved.id, loaded.id
        assertEquals saved.lastUpdate, loaded.lastUpdate
        assertEquals save.created, loaded.created
        assertEquals save.intValue, loaded.intValue
        assertEquals save.stringValue, loaded.stringValue
        assertEquals save.player, loaded.player
        assertNull loaded.completedTimestamp
        assertEquals save.featureData, loaded.featureData
        assertEquals save.features, loaded.features

        assertEquals 1, gameRepository.count()
        assertEquals 1, gameRepository.countByCreatedGreaterThan(start.minusSeconds(1))
        assertEquals 0, gameRepository.countByCreatedGreaterThan(loaded.created)
    }

    @Test
    void testCanUpdateAGame() {
        SimpleSinglePlayerGame initial, update, updated, loaded
        initial = new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1)
        initial = (SimpleSinglePlayerGame) gameRepository.save(initial)

        update = (SimpleSinglePlayerGame) gameRepository.findById(initial.id).get()
        update.stringValue = update.stringValue + 'Z'
        update.completedTimestamp = Instant.now()
        update.intValue = update.intValue * 2
        update.features.addAll(['HG', '34'])
        update.featureData.put('rr', new Long(3))
        updated = (SimpleSinglePlayerGame) gameRepository.save(update)
        assertNotNull updated
        assertEquals initial.id, updated.id
        assertTrue updated.lastUpdate > initial.lastUpdate
        assertEquals initial.created, updated.created
        assertEquals update.intValue, updated.intValue
        assertEquals update.stringValue, updated.stringValue
        assertEquals update.player, updated.player
        assertEquals update.completedTimestamp, updated.completedTimestamp
        assertEquals update.featureData, updated.featureData
        assertEquals update.features, updated.features

        loaded = (SimpleSinglePlayerGame) gameRepository.findById(update.id).get()
        assertNotNull loaded
        assertEquals updated.id, loaded.id
        assertEquals updated.lastUpdate, loaded.lastUpdate
        assertEquals updated.created, loaded.created
        assertEquals updated.intValue, loaded.intValue
        assertEquals updated.stringValue, loaded.stringValue
        assertEquals updated.player, loaded.player
        assertEquals updated.completedTimestamp, loaded.completedTimestamp
        assertEquals update.featureData, updated.featureData
        assertEquals update.features, updated.features

        assertEquals 1, gameRepository.count()
    }

    @Test
    void testFindGamesByPlayer() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1, gamePhase: GamePhase.Playing))
        SimpleSinglePlayerGame p1g2 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 10, stringValue: 'X', player: player1, gamePhase: GamePhase.Playing))
        SimpleSinglePlayerGame p1g3 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 15, stringValue: '2', player: player1, gamePhase: GamePhase.Challenged))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2, gamePhase: GamePhase.Challenged))

        List<SimpleSinglePlayerGame> p1g = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerId(player1.id)
        assertEquals 3, p1g.size()
        assertTrue p1g.contains(p1g1)
        assertTrue p1g.contains(p1g2)
        assertTrue p1g.contains(p1g3)
        List<SimpleSinglePlayerGame> p2g = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerId(player2.id)
        assertEquals 1, p2g.size()
        assertTrue p2g.contains(p2g1)

        assertEquals 4, StreamSupport.stream(gameRepository.findAll().spliterator(), false).count()

        Sort sort = new Sort(Sort.Direction.DESC, ["lastUpdate", "created"])
        PageRequest page = new PageRequest(0, 20, sort)
        List<SimpleSinglePlayerGame> by = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(player1.id, GamePhase.Playing, ((Instant) p1g1.created).minusSeconds(24 * 60 * 60), page)
        assertEquals 2, by.size()
        assertTrue by.contains(p1g1)
        assertTrue by.contains(p1g2)
    }

    @Test
    void testSavesCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.save(p1g1)
        assertEquals p1g1, cache.get(p1g1.id).get()
        p1g1.intValue = 150
        gameRepository.saveAll([p1g1, p2g1])
        assertEquals 150, ((SimpleSinglePlayerGame) cache.get(p1g1.id).get()).intValue
        assertEquals p2g1, cache.get(p2g1.id).get()
    }

    @Test
    void testDeleteAllCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.saveAll([p1g1, p2g1])
        assertEquals p1g1, cache.get(p1g1.id).get()
        assertEquals p2g1, cache.get(p2g1.id).get()

        gameRepository.deleteAll()

        assertNull cache.get(p1g1.id)
        assertNull cache.get(p2g1.id)
    }

    @Test
    void testSingleDeletesForCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.saveAll([p1g1, p2g1])
        assertEquals p1g1, cache.get(p1g1.id).get()
        assertEquals p2g1, cache.get(p2g1.id).get()

        gameRepository.delete((Game) p1g1)
        gameRepository.deleteById(p2g1.id)

        assertNull cache.get(p1g1.id)
        assertNull cache.get(p2g1.id)
    }

    @Test
    void testFindsCreatedBefore() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))

        List<SimpleSinglePlayerGame> games
        games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(p1g1.created)
        assertFalse games.contains(p1g1)
        assertFalse games.contains(p2g1)

        games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(Instant.now())
        assertTrue games.contains(p1g1)
        assertTrue games.contains(p2g1)
    }

    @Test
    void testDeleteCreatedBefore() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1, created: Instant.now()))
        Thread.sleep(100)
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2, created: Instant.now()))

        assertTrue gameRepository.findById(p1g1.id).present
        assertTrue gameRepository.findById(p2g1.id).present

        assertTrue 1 <= gameRepository.deleteByCreatedLessThan(p2g1.created)

        assertTrue gameRepository.findById(p2g1.id).present
        assertFalse gameRepository.findById(p1g1.id).present
    }

    @Test
    void testIterableDeletesForCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.saveAll([p1g1, p2g1])
        assertEquals p1g1, cache.get(p1g1.id).get()
        assertEquals p2g1, cache.get(p2g1.id).get()

        gameRepository.deleteAll([p1g1, p2g1] as List<Game>)

        assertNull cache.get(p1g1.id)
        assertNull cache.get(p2g1.id)
    }

    @Test
    void testCacheReallyHit() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        gameRepository.save(p1g1)
        assertEquals(p1g1, cache.get(p1g1.id).get())

        MongoOperations operations = context.getBean(MongoOperations.class)
        operations.remove(Query.query(Criteria.where("_id").is(p1g1.id)), GAMES_COLLECTION_NAME)

        assertEquals(p1g1, gameRepository.findById(p1g1.id).get())

        cache.clear()
        assertNull cache.get(p1g1.id)
        assertFalse gameRepository.findById(p1g1.id).present
    }

    @Test
    void testPlayerCount() {
        assertEquals 2L, playerRepository.count()
    }
}
