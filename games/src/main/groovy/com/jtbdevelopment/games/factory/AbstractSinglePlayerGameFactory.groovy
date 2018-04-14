package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.SinglePlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/4/2015
 * Time: 8:43 PM
 */
@CompileStatic
abstract class AbstractSinglePlayerGameFactory<IMPL extends SinglePlayerGame, FEATURES> extends AbstractGameFactory<IMPL> implements SinglePlayerGameFactory<IMPL, FEATURES> {
    AbstractSinglePlayerGameFactory(
            final List<GameInitializer<IMPL>> gameInitializers,
            final List<GameValidator<IMPL>> gameValidators) {
        super(gameInitializers, gameValidators)
    }

    IMPL createGame(
            final Set<FEATURES> features,
            final Player player) {
        IMPL game = createFreshGame(features, player)

        prepareGame(game)
        game
    }

    IMPL createGame(final IMPL previousGame) {
        IMPL game = createFreshGame(previousGame.features, previousGame.player)
        copyFromPreviousGame(previousGame, game)
        prepareGame(game)
        game
    }

    protected IMPL createFreshGame(final Set<FEATURES> features,
                                   final Player player) {
        IMPL game = newGame()
        game.player = player
        game.version = null
        game.features.addAll(features)
        game.gamePhase = GamePhase.Setup
        game
    }
}
