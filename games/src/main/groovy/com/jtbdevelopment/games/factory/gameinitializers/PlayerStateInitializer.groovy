package com.jtbdevelopment.games.factory.gameinitializers

import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 11/4/14
 * Time: 7:01 AM
 */
@Component
@CompileStatic
class PlayerStateInitializer implements GameInitializer<Game> {
    @Override
    void initializeGame(final Game game) {
        if (game instanceof MultiPlayerGame) {
            MultiPlayerGame multiPlayerGame = (MultiPlayerGame) game
            game.playerStates.put(game.initiatingPlayer, PlayerState.Accepted)
            game.players.findAll { Player it -> multiPlayerGame.initiatingPlayer != it.id }.each {
                Player it ->
                    multiPlayerGame.playerStates[it.id] = PlayerState.Pending
            }
        }
    }

    final int order = DEFAULT_ORDER
}
