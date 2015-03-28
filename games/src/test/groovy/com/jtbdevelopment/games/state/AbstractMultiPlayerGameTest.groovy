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
        assert game.initiatingPlayer == null
        assert game.players.isEmpty()
        assert game.playerStates.isEmpty()
        assert game.declinedTimestamp == null
    }
}
