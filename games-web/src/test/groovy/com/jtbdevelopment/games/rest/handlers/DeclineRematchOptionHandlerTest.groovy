package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMPGame
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException
import com.jtbdevelopment.games.state.GamePhase

/**
 * Date: 8/23/15
 * Time: 5:17 PM
 */
class DeclineRematchOptionHandlerTest extends GameCoreTestCase {
    DeclineRematchOptionHandler handler = new DeclineRematchOptionHandler()

    void testThrowsExceptionIfGameNotInRoundOverPhase() {
        GamePhase.values().find {
            GamePhase gp ->
                gp != GamePhase.RoundOver
        }.each {
            GamePhase gp ->
                StringMPGame game = new StringMPGame(gamePhase: gp)
                shouldFail(GameIsNotAvailableToRematchException.class, {
                    handler.handleActionInternal(null, game, null)
                })
        }
    }

    void testMarksGameAsNextRoundStarted() {
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.RoundOver)
        assert game.is(handler.handleActionInternal(null, game, null))
        assert GamePhase.NextRoundStarted == game.gamePhase
    }
}
