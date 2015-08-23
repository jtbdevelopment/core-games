package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.exceptions.input.GameIsNotAvailableToRematchException
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
                GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: gp)
                shouldFail(GameIsNotAvailableToRematchException.class, {
                    handler.handleActionInternal(null, game, null)
                })
        }
    }

    void testMarksGameAsNextRoundStarted() {
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.RoundOver)
        assert game.is(handler.handleActionInternal(null, game, null))
        assert GamePhase.NextRoundStarted == game.gamePhase
    }
}
