package com.jtbdevelopment.games.mongo.games

import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.caching.CacheConstants
import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.games.PlayerState
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.mongo.games.utility.SimpleMultiPlayerGame
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.mongodb.DBCollection
import groovy.transform.CompileStatic
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
@CompileStatic
class MongoMultiPlayerGamesIntegration extends AbstractMongoIntegration {
    private static final String GAMES_COLLECTION_NAME = 'multi'
    private DBCollection collection
    private ZoneId GMT = ZoneId.of("GMT")

    MongoPlayerRepository playerRepository
    AbstractMultiPlayerGameRepository gameRepository
    MongoPlayer player1, player2, player3, player4
    CacheManager cacheManager
    Cache cache

    @Before
    void setup() {
        assert db.collectionExists(GAMES_COLLECTION_NAME)
        collection = db.getCollection(GAMES_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        gameRepository = context.getBean(AbstractMultiPlayerGameRepository.class)

        gameRepository.deleteAll()
        playerRepository.deleteAll()

        player1 = (MongoPlayer) playerRepository.save(new MongoPlayer(source: "M", sourceId: "2"))
        player2 = (MongoPlayer) playerRepository.save(new MongoPlayer(source: "M", sourceId: "1"))
        player3 = (MongoPlayer) playerRepository.save(new MongoPlayer(source: "X", sourceId: "3"))
        player4 = (MongoPlayer) playerRepository.save(new MongoPlayer(source: "Y", sourceId: "2"))

        cacheManager = context.getBean(CacheManager.class)
        cache = cacheManager.getCache(CacheConstants.GAME_ID_CACHE)
    }

    @Test
    void testCanCreateGameAndReloadIt() {
        SimpleMultiPlayerGame save, saved, loaded
        save = new SimpleMultiPlayerGame(
                intValue: 5,
                stringValue: 'X',
                initiatingPlayer: player1.id,
                players: [player1, player2] as List<Player>,
                playerStates: [(player1.id): PlayerState.Accepted, (player2.id): PlayerState.Pending],
                features: ['Y', 'Z'] as Set,
                featureData: ['Y': new Integer(3)] as Map<String, Object>)
        assert save.id == null
        assert save.created == null
        assert save.lastUpdate == null
        assert save.completedTimestamp == null
        assert save.declinedTimestamp == null
        saved = (SimpleMultiPlayerGame) gameRepository.save(save)
        assert saved
        assert saved.id != null
        assert saved.lastUpdate != null
        assert saved.created != null
        assert saved.intValue == save.intValue
        assert saved.stringValue == save.stringValue
        assert saved.players == save.players
        assert saved.playerStates == save.playerStates
        assert saved.initiatingPlayer == save.initiatingPlayer
        assert saved.completedTimestamp == null
        assert saved.declinedTimestamp == null
        assert saved.featureData == save.featureData
        assert saved.features == save.features

        loaded = (SimpleMultiPlayerGame) gameRepository.findOne(saved.id)
        assert loaded
        assert loaded.id == saved.id
        assert ((ZonedDateTime) loaded.lastUpdate).withZoneSameInstant(GMT) == ((ZonedDateTime) saved.lastUpdate).withZoneSameInstant(GMT)
        assert ((ZonedDateTime) loaded.created).withZoneSameInstant(GMT) == ((ZonedDateTime) save.created).withZoneSameInstant(GMT)
        assert loaded.intValue == save.intValue
        assert loaded.stringValue == save.stringValue
        assert loaded.players == save.players
        assert loaded.playerStates == save.playerStates
        assert loaded.initiatingPlayer == save.initiatingPlayer
        assert loaded.declinedTimestamp == null
        assert loaded.completedTimestamp == null
        assert loaded.featureData == save.featureData
        assert loaded.features == save.features

        assert gameRepository.count() == 1
    }

    @Test
    void testCanUpdateAGame() {
        SimpleMultiPlayerGame initial, update, updated, loaded
        initial = new SimpleMultiPlayerGame(
                intValue: 5,
                stringValue: 'X',
                initiatingPlayer: player1.id,
                players: [player1, player2] as List<Player>,
                playerStates: [(player1.id): PlayerState.Accepted, (player2.id): PlayerState.Pending])
        initial = (SimpleMultiPlayerGame) gameRepository.save(initial)

        update = (SimpleMultiPlayerGame) gameRepository.findOne(initial.id)
        update.stringValue = update.stringValue + 'Z'
        update.features.add('23')
        update.featureData.put('23', '23')
        update.completedTimestamp = ZonedDateTime.now()
        update.intValue = update.intValue * 2
        update.initiatingPlayer = player2.id
        update.playerStates = [(player1.id): PlayerState.Accepted, (player2.id): PlayerState.Rejected]
        update.players = [player1, player2, player4, player3] as List<Player>
        update.declinedTimestamp = ZonedDateTime.now()
        updated = (SimpleMultiPlayerGame) gameRepository.save(update)
        assert updated
        assert updated.id == initial.id
        assert ((ZonedDateTime) updated.lastUpdate).withZoneSameInstant(GMT).compareTo(((ZonedDateTime) initial.lastUpdate).withZoneSameInstant(GMT)) > 0
        assert ((ZonedDateTime) updated.created).withZoneSameInstant(GMT) == ((ZonedDateTime) initial.created).withZoneSameInstant(GMT)
        assert updated.intValue == update.intValue
        assert updated.stringValue == update.stringValue
        assert updated.players == update.players
        assert updated.playerStates == update.playerStates
        assert updated.initiatingPlayer == update.initiatingPlayer
        assert ((ZonedDateTime) updated.declinedTimestamp).withZoneSameInstant(GMT) == ((ZonedDateTime) update.declinedTimestamp).withZoneSameInstant(GMT)
        assert updated.completedTimestamp == update.completedTimestamp
        assert updated.featureData == update.featureData
        assert updated.features == updated.features

        loaded = (SimpleMultiPlayerGame) gameRepository.findOne(update.id)
        assert loaded
        assert loaded.id == updated.id
        assert ((ZonedDateTime) loaded.lastUpdate).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.lastUpdate).withZoneSameInstant(GMT)
        assert ((ZonedDateTime) loaded.created).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.created).withZoneSameInstant(GMT)
        assert loaded.intValue == updated.intValue
        assert loaded.stringValue == updated.stringValue
        assert loaded.players == updated.players
        assert loaded.playerStates == updated.playerStates
        assert loaded.initiatingPlayer == updated.initiatingPlayer
        assert ((ZonedDateTime) loaded.declinedTimestamp).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.declinedTimestamp).withZoneSameInstant(GMT)
        assert ((ZonedDateTime) loaded.completedTimestamp).withZoneSameInstant(GMT) == ((ZonedDateTime) updated.completedTimestamp).withZoneSameInstant(GMT)
        assert loaded.featureData == update.featureData
        assert loaded.features == updated.features

        assert gameRepository.count() == 1
    }

    @Test
    void testFindGamesByPlayer() {
        SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 5, stringValue: 'X', players: [player1, player2] as List<Player>))
        SimpleMultiPlayerGame p1g2 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 10, stringValue: 'X', players: [player1, player3] as List<Player>))
        SimpleMultiPlayerGame p1g3 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2] as List<Player>))
        SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 20, stringValue: '2', players: [player2, player4] as List<Player>))

        List<SimpleMultiPlayerGame> p1g = (List<SimpleMultiPlayerGame>) gameRepository.findByPlayersId(player1.id)
        assert p1g.size() == 3
        assert p1g.contains(p1g1)
        assert p1g.contains(p1g2)
        assert p1g.contains(p1g3)
        List<SimpleMultiPlayerGame> p2g = (List<SimpleMultiPlayerGame>) gameRepository.findByPlayersId(player2.id)
        assert p2g.size() == 3
        assert p2g.contains(p2g1)
        assert p2g.contains(p1g1)
        assert p2g.contains(p1g3)

        assert gameRepository.findAll().collect { it }.size() == 4
    }

    @Test
    void testSavesCache() {
        SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2] as List<Player>))
        SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 20, stringValue: '2', players: [player2, player4] as List<Player>))
        gameRepository.save(p1g1)
        assert cache.get(p1g1.id).get() == p1g1
        p1g1.intValue = 150
        gameRepository.save([p1g1, p2g1])
        assert ((SimpleMultiPlayerGame) cache.get(p1g1.id).get()).intValue == 150
        assert cache.get(p2g1.id).get() == p2g1
    }

    @Test
    void testDeleteAllCache() {
        SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2] as List<Player>))
        SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 20, stringValue: '2', players: [player2, player4] as List<Player>))
        gameRepository.save([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.deleteAll()

        assert cache.get(p1g1.id) == null
        assert cache.get(p2g1.id) == null
    }

    @Test
    void testSingleDeletesForCache() {
        SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2] as List<Player>))
        SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 20, stringValue: '2', players: [player2, player4] as List<Player>))
        gameRepository.save([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.delete((Game) p1g1)
        gameRepository.delete(p2g1.id)

        assert cache.get(p1g1.id) == null
        assert cache.get(p2g1.id) == null
    }

    @Test
    void testIterableDeletesForCache() {
        SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2] as List<Player>))
        SimpleMultiPlayerGame p2g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 20, stringValue: '2', players: [player2, player4] as List<Player>))
        gameRepository.save([p1g1, p2g1])
        assert cache.get(p1g1.id).get() == p1g1
        assert cache.get(p2g1.id).get() == p2g1

        gameRepository.delete([p1g1, p2g1] as List<Game>)

        assert cache.get(p1g1.id) == null
        assert cache.get(p2g1.id) == null
    }

    @Test
    void testCacheReallyHit() {
        SimpleMultiPlayerGame p1g1 = (SimpleMultiPlayerGame) gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2] as List<Player>))
        gameRepository.save(p1g1)
        assert cache.get(p1g1.id).get() == p1g1

        MongoOperations operations = context.getBean(MongoOperations.class)
        operations.remove(Query.query(Criteria.where("_id").is(p1g1.id)), GAMES_COLLECTION_NAME)

        assert gameRepository.findOne(p1g1.id) == p1g1

        cache.clear()
        assert cache.get(p1g1.id) == null
        assert gameRepository.findOne(p1g1.id) == null
    }
}
