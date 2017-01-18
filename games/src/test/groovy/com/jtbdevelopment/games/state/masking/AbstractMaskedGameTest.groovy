package com.jtbdevelopment.games.state.masking

/**
 * Date: 2/19/15
 * Time: 7:05 AM
 */
class AbstractMaskedGameTest extends GroovyTestCase {
    private static class TestMaskedGame extends AbstractMaskedGame {

    }

    void testInitializes() {
        def ID = 'ANID'
        TestMaskedGame game = new TestMaskedGame(id: ID)
        assert ID == game.idAsString
        assert game.features.isEmpty()
        assert game.featureData.isEmpty()
        assert game.playerImages.isEmpty()
        assert game.playerProfiles.isEmpty()
        assert game.players.isEmpty()
        assertNull game.completedTimestamp
        assertNull game.created
        assertNull game.lastUpdate
        assertNull game.version
        assert game.id == ID
    }
}
