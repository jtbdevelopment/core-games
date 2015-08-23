package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.exceptions.input.GameIsNotAvailableToRematchException
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 8/23/15
 * Time: 5:14 PM
 */
class DeclineRematchOptionHandler extends AbstractGameActionHandler<Object, MultiPlayerGame> {
    @Autowired
    protected AbstractMultiPlayerGameFactory gameFactory

    @Override
    protected MultiPlayerGame handleActionInternal(
            final Player player, final MultiPlayerGame game, final Object param) {
        if (game.gamePhase != GamePhase.RoundOver) {
            throw new GameIsNotAvailableToRematchException()
        }
        game.gamePhase = GamePhase.NextRoundStarted;
        return game
    }
}
