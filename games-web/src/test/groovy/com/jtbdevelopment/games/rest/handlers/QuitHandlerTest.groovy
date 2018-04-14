package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMPGame
import com.jtbdevelopment.games.rest.exceptions.GameIsNotPossibleToQuitNowException
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState

/**
 * Date: 4/8/2015
 * Time: 10:02 PM
 */
class QuitHandlerTest extends GameCoreTestCase {
    QuitHandler handler = new QuitHandler()

    void testExceptionsOnQuitRematchRematchedPhases() {
        [GamePhase.Quit, GamePhase.RoundOver, GamePhase.NextRoundStarted, GamePhase.Declined].each {
            StringMPGame game = new StringMPGame(gamePhase: it)
            try {
                handler.handleActionInternal(null, game, null)
                fail()
            } catch (GameIsNotPossibleToQuitNowException e) {
                //
            }
        }
    }

    void testQuitsGamesInOtherStates() {
        [GamePhase.Challenged, GamePhase.Setup, GamePhase.Playing].each {
            StringMPGame game = new StringMPGame(gamePhase: it, playerStates: [(PONE.id): PlayerState.Pending, (PTWO.id): PlayerState.Rejected])

            StringMPGame ret = handler.handleActionInternal(PTWO, game, null)

            assert game.is(ret)
            assert game.gamePhase == GamePhase.Quit
            assert game.playerStates == [(PONE.id): PlayerState.Pending, (PTWO.id): PlayerState.Quit]
        }
    }
}
