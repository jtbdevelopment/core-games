package com.jtbdevelopment.games.mongo.games

import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.games.PlayerState
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.mongo.games.utility.SimpleMultiPlayerGame
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
class MongoMultiPlayerGamesIntegration extends AbstractMongoIntegration {
    private static final String GAMES_COLLECTION_NAME = 'multi'
    private DBCollection collection
    private ZoneId GMT = ZoneId.of("GMT")

    MongoPlayerRepository playerRepository
    AbstractMultiPlayerGameRepository gameRepository
    MongoPlayer player1, player2, player3, player4

    @Before
    void setup() {
        assert db.collectionExists(GAMES_COLLECTION_NAME)
        collection = db.getCollection(GAMES_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        gameRepository = context.getBean(AbstractMultiPlayerGameRepository.class)

        gameRepository.deleteAll()
        playerRepository.deleteAll()

        player1 = playerRepository.save(new MongoPlayer())
        player2 = playerRepository.save(new MongoPlayer())
        player3 = playerRepository.save(new MongoPlayer())
        player4 = playerRepository.save(new MongoPlayer())
    }

    @Test
    void testCanCreateGameAndReloadIt() {
        SimpleMultiPlayerGame save, saved, loaded
        save = new SimpleMultiPlayerGame(
                intValue: 5,
                stringValue: 'X',
                initiatingPlayer: player1.id,
                players: [player1, player2],
                playerStates: [(player1.id): PlayerState.Accepted, (player2.id): PlayerState.Pending],
                features: ['Y', 'Z'],
                featureData: ['Y': new Integer(3)])
        assert save.id == null
        assert save.created == null
        assert save.lastUpdate == null
        assert save.completedTimestamp == null
        assert save.declinedTimestamp == null
        saved = gameRepository.save(save)
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

        loaded = gameRepository.findOne(saved.id)
        assert loaded
        assert loaded.id == saved.id
        assert loaded.lastUpdate.withZoneSameInstant(GMT) == saved.lastUpdate.withZoneSameInstant(GMT)
        assert loaded.created.withZoneSameInstant(GMT) == save.created.withZoneSameInstant(GMT)
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
                players: [player1, player2],
                playerStates: [(player1.id): PlayerState.Accepted, (player2.id): PlayerState.Pending])
        initial = gameRepository.save(initial)

        update = gameRepository.findOne(initial.id)
        update.stringValue = update.stringValue + 'Z'
        update.features.add('23')
        update.featureData.put('23', '23')
        update.completedTimestamp = ZonedDateTime.now()
        update.intValue = update.intValue * 2
        update.initiatingPlayer = player2.id
        update.playerStates = [(player1.id): PlayerState.Accepted, (player2.id): PlayerState.Rejected]
        update.players = [player1, player2, player4, player3]
        update.declinedTimestamp = ZonedDateTime.now()
        updated = gameRepository.save(update)
        assert updated
        assert updated.id == initial.id
        assert updated.lastUpdate.withZoneSameInstant(GMT).compareTo(initial.lastUpdate.withZoneSameInstant(GMT)) > 0
        assert updated.created.withZoneSameInstant(GMT) == initial.created.withZoneSameInstant(GMT)
        assert updated.intValue == update.intValue
        assert updated.stringValue == update.stringValue
        assert updated.players == update.players
        assert updated.playerStates == update.playerStates
        assert updated.initiatingPlayer == update.initiatingPlayer
        assert updated.declinedTimestamp.withZoneSameInstant(GMT) == update.declinedTimestamp.withZoneSameInstant(GMT)
        assert updated.completedTimestamp == update.completedTimestamp
        assert updated.featureData == update.featureData
        assert updated.features == updated.features

        loaded = gameRepository.findOne(update.id)
        assert loaded
        assert loaded.id == updated.id
        assert loaded.lastUpdate.withZoneSameInstant(GMT) == updated.lastUpdate.withZoneSameInstant(GMT)
        assert loaded.created.withZoneSameInstant(GMT) == updated.created.withZoneSameInstant(GMT)
        assert loaded.intValue == updated.intValue
        assert loaded.stringValue == updated.stringValue
        assert loaded.players == updated.players
        assert loaded.playerStates == updated.playerStates
        assert loaded.initiatingPlayer == updated.initiatingPlayer
        assert loaded.declinedTimestamp.withZoneSameInstant(GMT) == updated.declinedTimestamp.withZoneSameInstant(GMT)
        assert loaded.completedTimestamp.withZoneSameInstant(GMT) == updated.completedTimestamp.withZoneSameInstant(GMT)
        assert loaded.featureData == update.featureData
        assert loaded.features == updated.features

        assert gameRepository.count() == 1
    }

    @Test
    void testFindGamesByPlayer() {
        SimpleMultiPlayerGame p1g1 = gameRepository.save(new SimpleMultiPlayerGame(intValue: 5, stringValue: 'X', players: [player1, player2]))
        SimpleMultiPlayerGame p1g2 = gameRepository.save(new SimpleMultiPlayerGame(intValue: 10, stringValue: 'X', players: [player1, player3]))
        SimpleMultiPlayerGame p1g3 = gameRepository.save(new SimpleMultiPlayerGame(intValue: 15, stringValue: '2', players: [player1, player4, player2]))
        SimpleMultiPlayerGame p2g1 = gameRepository.save(new SimpleMultiPlayerGame(intValue: 20, stringValue: '2', players: [player2, player4]))

        List<SimpleMultiPlayerGame> p1g = gameRepository.findByPlayersId(player1.id)
        assert p1g.size() == 3
        assert p1g.contains(p1g1)
        assert p1g.contains(p1g2)
        assert p1g.contains(p1g3)
        List<SimpleMultiPlayerGame> p2g = gameRepository.findByPlayersId(player2.id)
        assert p2g.size() == 3
        assert p2g.contains(p2g1)
        assert p2g.contains(p1g1)
        assert p2g.contains(p1g3)

        assert gameRepository.findAll().size() == 4
    }
}
