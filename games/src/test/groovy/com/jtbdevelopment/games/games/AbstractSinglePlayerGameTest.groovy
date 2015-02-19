package com.jtbdevelopment.games.games

/**
 * Date: 1/7/15
 * Time: 6:57 AM
 */
class AbstractSinglePlayerGameTest extends GroovyTestCase {
    private static class FloatSinglePlayerGame extends AbstractSinglePlayerGame<Float, Object> {
        Float id

        @Override
        String getIdAsString() {
            return id?.toString()
        }
    }

    void testConstructor() {
        FloatSinglePlayerGame game = new FloatSinglePlayerGame()

        assert game.player == null
    }
}
