package com.jtbdevelopment.games.state

import com.jtbdevelopment.games.players.Player

import java.beans.Transient

/**
 * Date: 1/7/15
 * Time: 6:54 AM
 */
class AbstractMultiPlayerGameTest extends GroovyTestCase {
    private static class IntegerMultiPlayerGame extends AbstractMultiPlayerGame<Integer, Object> {
        Integer id
        Integer previousId

        @Override
        @Transient
        String getIdAsString() {
            return id?.toString()
        }

        @Override
        @Transient
        String getPreviousIdAsString() {
            return previousId?.toString()
        }
    }

    void testConstructor() {
        IntegerMultiPlayerGame game = new IntegerMultiPlayerGame()
        assertNull game.initiatingPlayer
        assert game.players.isEmpty()
        assert game.playerStates.isEmpty()
        assertNull game.declinedTimestamp
        assertNull game.rematchTimestamp
    }

    void testAllPlayers() {
        def p1 = [] as Player
        def p2 = [] as Player
        IntegerMultiPlayerGame game = new IntegerMultiPlayerGame(players: [p1, p2])
        assert [p1, p2] == game.players
        assert [p1, p2] == game.allPlayers
    }
}
