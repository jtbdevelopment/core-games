package com.jtbdevelopment.games.mongo.state

import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.dao.caching.CacheConstants
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.mongo.state.utility.SimpleSinglePlayerGame
import com.jtbdevelopment.games.state.Game
import com.mongodb.DBCollection
import org.junit.Before
import org.junit.Test
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/10/15
 * Time: 2:35 PM
 */
class MongoSinglePlayerGamesIntegration extends AbstractMongoIntegration {
    private static final String GAMES_COLLECTION_NAME = 'single'
    private DBCollection collection
    private ZoneId GMT = ZoneId.of("GMT")

    MongoPlayerRepository playerRepository
    AbstractSinglePlayerGameRepository gameRepository
    MongoPlayer player1, player2
    CacheManager cacheManager
    Cache cache
    ZonedDateTime start

    @Before
    void setup() {
        start = ZonedDateTime.now(ZoneId.of("GMT"))
        assert db.collectionExists(GAMES_COLLECTION_NAME)
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


        loaded = (SimpleSinglePlayerGame) gameRepository.findOne(saved.id)
        assert loaded
        assert loaded.id == saved.id
        assert ((ZonedDateTime) loaded.lastUpdate).withZoneSameInstant(GMT) == ((ZonedDateTime) saved.lastUpdate).withZoneSameInstant(GMT)
        assert ((ZonedDateTime) loaded.created).withZoneSameInstant(GMT) == ((ZonedDateTime) save.created).withZoneSameInstant(GMT)
        assert loaded.intValue == save.intValue
        assert loaded.stringValue == save.stringValue
        assert loaded.player == save.player
        assert loaded.completedTimestamp == null
        assert loaded.featureData == save.featureData
        assert loaded.features == save.features

        assert gameRepository.count() == 1
        assert gameRepository.countByCreatedGreaterThan(start) == 1
        assert gameRepository.countByCreatedGreaterThan((ZonedDateTime) loaded.created) == 0
    }

    @Test
    void testCanUpdateAGame() {
        SimpleSinglePlayerGame initial, update, updated, loaded
        initial = new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1)
        initial = (SimpleSinglePlayerGame) gameRepository.save(initial)

        update = (SimpleSinglePlayerGame) gameRepository.findOne(initial.id)
        update.stringValue = update.stringValue + 'Z'
        update.completedTimestamp = ZonedDateTime.now()
        update.intValue = update.intValue * 2
        update.features.addAll(['HG', '34'])
        update.featureData.put('rr', new Long(3))
        updated = (SimpleSinglePlayerGame) gameRepository.save(update)
        assert updated
        assert updated.id == initial.id
        assert ((ZonedDateTime) updated.lastUpdate).withZoneSameInstant(GMT).compareTo(((ZonedDateTime) initial.lastUpdate).withZoneSameInstant(GMT)) > 0
        assert ((ZonedDateTime) updated.created).withZoneSameInstant(GMT) == ((ZonedDateTime) initial.created).withZoneSameInstant(GMT)
        assert updated.intValue == update.intValue
        assert updated.stringValue == update.stringValue
        assert updated.player == update.player
        assert updated.completedTimestamp == update.completedTimestamp
        assert updated.featureData == update.featureData
        assert updated.features == update.features

        loaded = (SimpleSinglePlayerGame) gameRepository.findOne(update.id)
        assert loaded
        assert loaded.id == updated.id
        assert ((ZonedDateTime) loaded.lastUpdate).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.lastUpdate).withZoneSameInstant(GMT)
        assert ((ZonedDateTime) loaded.created).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.created).withZoneSameInstant(GMT)
        assert loaded.intValue == updated.intValue
        assert loaded.stringValue == updated.stringValue
        assert loaded.player == updated.player
        assert ((ZonedDateTime) loaded.completedTimestamp).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.completedTimestamp).withZoneSameInstant(GMT)
        assert updated.featureData == update.featureData
        assert updated.features == update.features

        assert gameRepository.count() == 1
    }

    @Test
    void testFindGamesByPlayer() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p1g2 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 10, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p1g3 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 15, stringValue: '2', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))

        List<SimpleSinglePlayerGame> p1g = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerId(player1.id)
        assert p1g.size() == 3
        assert p1g.contains(p1g1)
        assert p1g.contains(p1g2)
        assert p1g.contains(p1g3)
        List<SimpleSinglePlayerGame> p2g = (List<SimpleSinglePlayerGame>) gameRepository.findByPlayerId(player2.id)
        assert p2g.size() == 1
        assert p2g.contains(p2g1)

        assert gameRepository.findAll().collect { it }.size() == 4
    }

    @Test
    void testSavesCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.save(p1g1)
        assert cache.get(p1g1.id).get() == p1g1
        p1g1.intValue = 150
        gameRepository.save([p1g1, p2g1])
        assert ((SimpleSinglePlayerGame) cache.get(p1g1.id).get()).intValue == 150
        assert cache.get(p2g1.id).get() == p2g1
    }

    @Test
    void testDeleteAllCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.save([p1g1, p2g1])
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
        gameRepository.save([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.delete((Game) p1g1)
        gameRepository.delete(p2g1.id)

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

        games = (List<SimpleSinglePlayerGame>) gameRepository.findByCreatedLessThan(ZonedDateTime.now(GMT))
        assert games.contains(p1g1)
        assert games.contains(p2g1)
    }

    @Test
    void testDeleteCreatedBefore() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1, created: ZonedDateTime.now(GMT)))
        Thread.sleep(100)
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2, created: ZonedDateTime.now(GMT)))

        assert gameRepository.findOne(p1g1.id)
        assert gameRepository.findOne(p2g1.id)

        assert 1 <= gameRepository.deleteByCreatedLessThan((ZonedDateTime) p2g1.created.withZoneSameInstant(GMT))

        assert gameRepository.findOne(p2g1.id)
        assert null == gameRepository.findOne(p1g1.id)
    }

    @Test
    void testIterableDeletesForCache() {
        SimpleSinglePlayerGame p1g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p2g1 = (SimpleSinglePlayerGame) gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))
        gameRepository.save([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.delete([p1g1, p2g1] as List<Game>)

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

        assert gameRepository.findOne(p1g1.id) == p1g1

        cache.clear()
        assert cache.get(p1g1.id) == null
        assert gameRepository.findOne(p1g1.id) == null
    }

    @Test
    void testPlayerCount() {
        assert 2L == playerRepository.count()
    }
}
