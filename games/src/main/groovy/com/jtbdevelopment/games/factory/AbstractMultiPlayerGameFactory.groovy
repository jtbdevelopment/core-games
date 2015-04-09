package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic

/**
 * Date: 4/4/2015
 * Time: 8:43 PM
 */
@CompileStatic
abstract class AbstractMultiPlayerGameFactory<IMPL extends MultiPlayerGame, FEATURES> extends AbstractGameFactory<IMPL> implements MultiPlayerGameFactory<IMPL, FEATURES> {

    public IMPL createGame(
            final Set<FEATURES> features,
            final List<Player> players,
            final Player initiatingPlayer) {
        IMPL game = createFreshGame(features, players, initiatingPlayer)

        prepareGame(game)
        game
    }

    public IMPL createGame(final IMPL previousGame, final Player initiatingPlayer) {
        List<Player> players = rotatePlayers(previousGame)
        IMPL game = createFreshGame(previousGame.features, players, initiatingPlayer)
        copyFromPreviousGame(previousGame, game)
        prepareGame(game)
        game
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected List<Player> rotatePlayers(final IMPL previousGame) {
        List<Player> players = []
        players.addAll(previousGame.players)
        players.add(players.remove(0))
        players
    }

    protected IMPL createFreshGame(final Set<FEATURES> features,
                                   final List<Player> players,
                                   final Player initiatingPlayer) {
        IMPL game = newGame()
        game.gamePhase = GamePhase.Challenged
        game.version = null
        game.features.addAll(features)
        game.initiatingPlayer = initiatingPlayer.id
        game.players.addAll(players)
        if (!game.players.contains(initiatingPlayer)) {
            game.players.add(initiatingPlayer)
        }
        game
    }
}
