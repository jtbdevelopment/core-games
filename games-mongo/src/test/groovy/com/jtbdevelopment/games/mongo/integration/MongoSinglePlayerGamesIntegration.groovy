package com.jtbdevelopment.games.mongo.integration

import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.mongo.integration.games.SimpleSinglePlayerGame
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.mongodb.DBCollection
import org.junit.Before
import org.junit.Test

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 1/10/15
 * Time: 2:35 PM
 */
class MongoSinglePlayerGamesIntegration extends AbstractMongoIntegration {
    private static final String GAMES_COLLECTION_NAME = 'single'
    private static final String PLAYER_COLLECTION_NAME = 'player'
    private DBCollection collection
    private ZoneId GMT = ZoneId.of("GMT")

    MongoPlayerRepository playerRepository
    AbstractSinglePlayerGameRepository gameRepository
    MongoPlayer player1, player2

    @Before
    void setup() {
        assert db.collectionExists(GAMES_COLLECTION_NAME)
        collection = db.getCollection(GAMES_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        gameRepository = context.getBean(AbstractSinglePlayerGameRepository.class)
        player1 = new MongoPlayer()
        player1 = playerRepository.save(player1)
        player2 = new MongoPlayer()
        player2 = playerRepository.save(player2)

        gameRepository.deleteAll()
    }

    @Test
    void testCanCreateGameAndReloadIt() {
        SimpleSinglePlayerGame save, saved, loaded
        save = new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1)
        assert save.id == null
        assert save.created == null
        //assert save.lastUpdate == null
        assert save.completedTimestamp == null
        saved = gameRepository.save(save)
        assert saved
        assert saved.id != null
        assert saved.lastUpdate != null
        assert saved.created != null
        assert saved.intValue == save.intValue
        assert saved.stringValue == save.stringValue
        assert saved.player == save.player
        assert saved.completedTimestamp == null

        loaded = gameRepository.findOne(saved.id)
        assert loaded
        assert loaded.id == saved.id
        assert loaded.lastUpdate.withZoneSameInstant(GMT) == saved.lastUpdate.withZoneSameInstant(GMT)
        assert loaded.created.withZoneSameInstant(GMT) == save.created.withZoneSameInstant(GMT)
        assert loaded.intValue == save.intValue
        assert loaded.stringValue == save.stringValue
        assert loaded.player == save.player
        assert loaded.completedTimestamp == null

        assert gameRepository.count() == 1
    }

    @Test
    void testCanUpdateAGame() {
        SimpleSinglePlayerGame initial, update, updated, loaded
        initial = new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1)
        initial = gameRepository.save(initial)

        update = gameRepository.findOne(initial.id)
        update.stringValue = update.stringValue + 'Z'
        update.completedTimestamp = ZonedDateTime.now()
        update.intValue = update.intValue * 2
        updated = gameRepository.save(update)
        assert updated
        assert updated.id == initial.id
        assert updated.lastUpdate.withZoneSameInstant(GMT).compareTo(initial.lastUpdate.withZoneSameInstant(GMT)) > 0
        assert updated.created.withZoneSameInstant(GMT) == initial.created.withZoneSameInstant(GMT)
        assert updated.intValue == update.intValue
        assert updated.stringValue == update.stringValue
        assert updated.player == update.player
        assert updated.completedTimestamp == update.completedTimestamp

        loaded = gameRepository.findOne(update.id)
        assert loaded
        assert loaded.id == updated.id
        assert loaded.lastUpdate.withZoneSameInstant(GMT) == updated.lastUpdate.withZoneSameInstant(GMT)
        assert loaded.created.withZoneSameInstant(GMT) == updated.created.withZoneSameInstant(GMT)
        assert loaded.intValue == updated.intValue
        assert loaded.stringValue == updated.stringValue
        assert loaded.player == updated.player
        assert loaded.completedTimestamp.withZoneSameInstant(GMT) == updated.completedTimestamp.withZoneSameInstant(GMT)

        assert gameRepository.count() == 1
    }

    @Test
    void testFindGamesByPlayer() {
        SimpleSinglePlayerGame p1g1 = gameRepository.save(new SimpleSinglePlayerGame(intValue: 5, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p1g2 = gameRepository.save(new SimpleSinglePlayerGame(intValue: 10, stringValue: 'X', player: player1))
        SimpleSinglePlayerGame p1g3 = gameRepository.save(new SimpleSinglePlayerGame(intValue: 15, stringValue: '2', player: player1))
        SimpleSinglePlayerGame p2g1 = gameRepository.save(new SimpleSinglePlayerGame(intValue: 20, stringValue: '2', player: player2))

        List<SimpleSinglePlayerGame> p1g = gameRepository.findByPlayerId(player1.id)
        assert p1g.size() == 3
        assert p1g.contains(p1g1)
        assert p1g.contains(p1g2)
        assert p1g.contains(p1g3)
        List<SimpleSinglePlayerGame> p2g = gameRepository.findByPlayerId(player2.id)
        assert p2g.size() == 1
        assert p2g.contains(p2g1)

        assert gameRepository.findAll().size() == 4
    }
}
