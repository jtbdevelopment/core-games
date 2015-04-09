package com.jtbdevelopment.games.state

/**
 * Date: 1/7/15
 * Time: 6:54 AM
 */
class AbstractMultiPlayerGameTest extends GroovyTestCase {
    private static class IntegerMultiPlayerGame extends AbstractMultiPlayerGame<Integer, Object> {
        Integer id

        @Override
        String getIdAsString() {
            return id?.toString()
        }
    }

    void testConstructor() {
        IntegerMultiPlayerGame game = new IntegerMultiPlayerGame()
        assertNull game.initiatingPlayer
        assert game.players.isEmpty()
        assert game.playerStates.isEmpty()
        assertNull game.declinedTimestamp
        assertNull game.gamePhase
        assertNull game.rematchTimestamp
    }
}
