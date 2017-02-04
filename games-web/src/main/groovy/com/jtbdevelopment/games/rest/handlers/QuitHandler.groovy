package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.exceptions.GameIsNotPossibleToQuitNowException
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 11/28/2014
 * Time: 7:40 PM
 */
@CompileStatic
@Component
class QuitHandler extends AbstractGameActionHandler<Object, MultiPlayerGame> {
    @Override
    protected MultiPlayerGame handleActionInternal(
            final Player player, final MultiPlayerGame game, final Object param) {
        if (game.gamePhase == GamePhase.NextRoundStarted ||
                game.gamePhase == GamePhase.RoundOver ||
                game.gamePhase == GamePhase.Quit ||
                game.gamePhase == GamePhase.Declined) {
            throw new GameIsNotPossibleToQuitNowException()
        }

        game.gamePhase = GamePhase.Quit
        game.playerStates[player.id] = PlayerState.Quit
        game
    }
}
