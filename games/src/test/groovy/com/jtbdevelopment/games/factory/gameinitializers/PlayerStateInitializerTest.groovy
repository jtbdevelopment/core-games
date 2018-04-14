package com.jtbdevelopment.games.factory.gameinitializers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMPGame
import com.jtbdevelopment.games.StringSPGame
import com.jtbdevelopment.games.state.PlayerState

/**
 * Date: 4/4/2015
 * Time: 8:21 PM
 */
class PlayerStateInitializerTest extends GameCoreTestCase {
    PlayerStateInitializer playerStateInitializer = new PlayerStateInitializer()

    public void testInitializesAllPlayersToPendingAcceptingInitiatingPlayer() {
        StringMPGame game = new StringMPGame()
        def players = [PONE, PTWO, PTHREE, PFOUR]
        game.players = players
        game.initiatingPlayer = PTHREE.id
        playerStateInitializer.initializeGame(game)
        assert game.playerStates.size() == 4
        assert game.playerStates[(PTHREE.id)] == PlayerState.Accepted
        players.findAll { it != PTHREE }.each {
            assert game.playerStates[it.id] == PlayerState.Pending
        }
    }

    public void testIgnoresInitializesSingePlayerGame() {
        StringSPGame game = new StringSPGame()
        playerStateInitializer.initializeGame(game)
    }
}
