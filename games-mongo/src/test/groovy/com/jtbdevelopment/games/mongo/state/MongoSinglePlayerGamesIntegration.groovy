package com.jtbdevelopment.games.mongo.state

import com.jtbdevelopment.core.mongo.spring.AbstractCoreMongoConfiguration
import com.jtbdevelopment.core.mongo.spring.AbstractMongoNoSpringContextIntegration
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

    MongoPlayerRepository playerRepository
    AbstractSinglePlayerGameRepository gameRepository
    MongoPlayer player1, player2
    CacheManager cacheManager
    Cache cache
    Instant start
    static ApplicationContext context

    @SuppressWarnings("GroovyUnusedDeclaration")
    @BeforeClass
    static void setupAll() {
        setupMongo()
        context = new AnnotationConfigApplicationContext(MongoSinglePlayerGameIntegrationConfiguration.class)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
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
        assert save.id == null
        assert save.created == null
        assert save.lastUpdate == null
        assert save.completedTimestamp == null
        saved = (SimpleSinglePlayerGame) gameRepository.save(save)
        assert saved
        assert saved.id != null
        assert saved.lastUpdate != null
        assert saved.created != null
        assert saved.intValue == save.intValue
        assert saved.stringValue == save.stringValue
        assert saved.player == save.player
        assert saved.completedTimestamp == null
        assert saved.featureData == save.featureData
        assert saved.features == save.features


        loaded = (SimpleSinglePlayerGame) gameRepository.findById(saved.id).get()
        assert loaded
        assert loaded.id == saved.id
        assert loaded.lastUpdate == saved.lastUpdate
        assert loaded.created == save.created
        assert loaded.intValue == save.intValue
        assert loaded.stringValue == save.stringValue
        assert loaded.player == save.player
        assert loaded.completedTimestamp == null
        assert loaded.featureData == save.featureData
        assert loaded.features == save.features

        assert gameRepository.count() == 1
        assert gameRepository.countByCreatedGreaterThan(start.minusSeconds(1)) == 1
        assert gameRepository.countByCreatedGreaterThan(loaded.created) == 0
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
        assert updated
        assert updated.id == initial.id
        assert updated.lastUpdate > initial.lastUpdate
        assert updated.created == initial.created
        assert updated.intValue == update.intValue
        assert updated.stringValue == update.stringValue
        assert updated.player == update.player
        assert updated.completedTimestamp == update.completedTimestamp
        assert updated.featureData == update.featureData
        assert updated.features == update.features

        loaded = (SimpleSinglePlayerGame) gameRepository.findById(update.id).get()
        assert loaded
        assert loaded.id == updated.id
        assert loaded.lastUpdate == updated.lastUpdate
        assert loaded.created == updated.created
        assert loaded.intValue == updated.intValue
        assert loaded.stringValue == updated.stringValue
        assert loaded.player == updated.player
        assert loaded.completedTimestamp == updated.completedTimestamp
        assert updated.featureData == update.featureData
        assert updated.features == update.features

        assert gameRepository.count() == 1
    }

    @Test
    void testFindGamesByPlayer() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1, gamePhase: GamePhase.Playing))
        SimpleSinglePlayerGame p1g2 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 10, stringValue: 'X', player: player1, gamePhase: GamePhase.Playing))
        SimpleSinglePlayerGame p1g3 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 15, stringValue: '2', player: player1, gamePhase: GamePhase.Challenged))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2, gamePhase: GamePhase.Challenged))

        List<SimpleSinglePlayerGame> p1g = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerId(player1.id)
        assert p1g.size() == 3
        assert p1g.contains(p1g1)
        assert p1g.contains(p1g2)
        assert p1g.contains(p1g3)
        List<SimpleSinglePlayerGame> p2g = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerId(player2.id)
        assert p2g.size() == 1
        assert p2g.contains(p2g1)

        assert gameRepository.findAll().collect { it }.size() == 4

        Sort sort = new Sort(Sort.Direction.DESC, ["lastUpdate", "created"])
        PageRequest page = new PageRequest(0, 20, sort)
        List<SimpleSinglePlayerGame> by = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(player1.id, GamePhase.Playing, ((Instant) p1g1.created).minusSeconds(24 * 60 * 60), page)
        assert 2 == by.size()
        assert by.contains(p1g1)
        assert by.contains(p1g2)
    }

    @Test
    void testSavesCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.save(p1g1)
        assert cache.get(p1g1.id).get() == p1g1
        p1g1.intValue = 150
        gameRepository.saveAll([p1g1, p2g1])
        assert ((SimpleSinglePlayerGame) cache.get(p1g1.id).get()).intValue == 150
        assert cache.get(p2g1.id).get() == p2g1
    }

    @Test
    void testDeleteAllCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.saveAll([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.deleteAll()

        assert cache.get(p1g1.id) == null
        assert cache.get(p2g1.id) == null
    }

    @Test
    void testSingleDeletesForCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.saveAll([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.delete((Game) p1g1)
        gameRepository.deleteById(p2g1.id)

        assert cache.get(p1g1.id) == null
        assert cache.get(p2g1.id) == null
    }

    @Test
    void testFindsCreatedBefore() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))

        List<SimpleSinglePlayerGame> games
        games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(p1g1.created)
        assert !games.contains(p1g1)
        assert !games.contains(p2g1)

        games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(Instant.now())
        assert games.contains(p1g1)
        assert games.contains(p2g1)
    }

    @Test
    void testDeleteCreatedBefore() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1, created: Instant.now()))
        Thread.sleep(100)
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2, created: Instant.now()))

        assert gameRepository.findById(p1g1.id).present
        assert gameRepository.findById(p2g1.id).present

        assert 1 <= gameRepository.deleteByCreatedLessThan(p2g1.created)

        assert gameRepository.findById(p2g1.id).present
        assert !gameRepository.findById(p1g1.id).present
    }

    @Test
    void testIterableDeletesForCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.saveAll([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.deleteAll([p1g1, p2g1] as List<Game>)

        assert cache.get(p1g1.id) == null
        assert cache.get(p2g1.id) == null
    }

    @Test
    void testCacheReallyHit() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        gameRepository.save(p1g1)
        assert cache.get(p1g1.id).get() == p1g1

        MongoOperations operations = context.getBean(MongoOperations.class)
        operations.remove(Query.query(Criteria.where("_id").is(p1g1.id)), GAMES_COLLECTION_NAME)

        assert gameRepository.findById(p1g1.id).get() == p1g1

        cache.clear()
        assert cache.get(p1g1.id) == null
        assert !gameRepository.findById(p1g1.id).present
    }

    @Test
    void testPlayerCount() {
        assert 2L == playerRepository.count()
    }
}
