package com.jtbdevelopment.games.games.masked

/**
 * Date: 2/19/15
 * Time: 7:05 AM
 */
class AbstractMaskedMultiPlayerGameTest extends GroovyTestCase {
    private static class TestMaskedGame extends AbstractMaskedMultiPlayerGame {

    }

    void testInitializes() {
        def ID = 'ANID'
        TestMaskedGame game = new TestMaskedGame(id: ID)
        assert ID == game.idAsString
        assert game.features.isEmpty()
        assert game.featureData.isEmpty()
        assert game.playerImages.isEmpty()
        assert game.playerProfiles.isEmpty()
        assert game.playerStates.isEmpty()
        assert game.players.isEmpty()
        assertNull game.maskedForPlayerID
        assertNull game.maskedForPlayerMD5
        assertNull game.initiatingPlayer
        assertNull game.completedTimestamp
        assertNull game.declinedTimestamp
        assertNull game.created
        assertNull game.lastUpdate
        assertNull game.version
        assert game.id == ID
    }
}
