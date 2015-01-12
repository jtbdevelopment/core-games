package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.mongodb.DBCollection
import org.junit.Before
import org.junit.Test

import java.time.ZoneId

/**
 * Date: 1/11/15
 * Time: 3:09 PM
 */
class MongoPlayerIntegration extends AbstractMongoIntegration {
    private static final String PLAYER_COLLECTION_NAME = 'player'
    private DBCollection collection
    private ZoneId GMT = ZoneId.of("GMT")

    MongoPlayerRepository playerRepository
    MongoPlayer player1, player2, player3, player4
    MongoManualPlayer manualPlayer
    MongoSystemPlayer systemPlayer

    protected MongoPlayer makeSimplePlayer(final String id, final boolean disabled = false) {
        return playerRepository.save(new MongoPlayer(
                source: "MADEUP",
                sourceId: "MADEUP" + id,
                displayName: id,
                disabled: disabled,
                imageUrl: "http://somewhere.com/image/" + id,
                profileUrl: "http://somewhere.com/profile/" + id))
    }

    @Before
    void setup() {
        assert db.collectionExists(PLAYER_COLLECTION_NAME)
        collection = db.getCollection(PLAYER_COLLECTION_NAME)

        playerRepository = context.getBean(MongoPlayerRepository.class)
        playerRepository.deleteAll()

        player1 = makeSimplePlayer('1')
        player2 = makeSimplePlayer('2')
        player3 = makeSimplePlayer('3', true)
        player4 = makeSimplePlayer('4')
        manualPlayer = playerRepository.save(new MongoManualPlayer(
                sourceId: "MADEUP" + "M",
                displayName: "M",
                disabled: false))
        systemPlayer = playerRepository.save(new MongoSystemPlayer(
                sourceId: "MADEUP" + "S",
                displayName: "S",
                disabled: false))
    }

    @Test
    void testFindBySourceAndDisabled() {
        assert playerRepository.findBySourceAndDisabled(systemPlayer.source, true).isEmpty()
        assert playerRepository.findBySourceAndDisabled(systemPlayer.source, false) == [systemPlayer]
        assert playerRepository.findBySourceAndDisabled(manualPlayer.source, true).isEmpty()
        assert playerRepository.findBySourceAndDisabled(manualPlayer.source, false) == [manualPlayer]
        assert playerRepository.findBySourceAndDisabled(player1.source, false) as Set == [player1, player2, player4] as Set
        assert playerRepository.findBySourceAndDisabled(player1.source, true) == [player3]
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
        List<Player> players = playerRepository.findByMd5In([player1.md5, player3.md5, systemPlayer.md5, 'JUNK'])
        assert players.size() == 3
        assert players.contains(player1)
        assert players.contains(player3)
        assert players.contains(systemPlayer)
    }
}
