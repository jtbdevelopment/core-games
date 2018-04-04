package com.jtbdevelopment.games.factory.gamevalidators

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.factory.GameValidator
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.SinglePlayerGame
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 11/8/14
 * Time: 8:55 AM
 */
@Component
@CompileStatic
class PlayersActiveGameValidator implements GameValidator<Game> {
    public static final String ERROR_MESSAGE = "Game contains inactive players."
    @Autowired
    AbstractPlayerRepository playerRepository

    @Override
    boolean validateGame(final Game game) {
        if (game instanceof MultiPlayerGame) {
            Iterable<Player> loaded = playerRepository.findAllById(game.allPlayers.collect { Player player -> player.id })

            Collection<Player> all = loaded.findAll {
                Player player ->
                    !player.disabled
            }
            Collection<Serializable> loadedActivePlayers = all.collect {
                Player player ->
                    player.id
            }
            return loadedActivePlayers.size() == game.allPlayers.size()
        } else if (game instanceof SinglePlayerGame) {
            Optional<Player> loaded = playerRepository.findById(((SinglePlayerGame) game).player.id)
            return (!loaded.present ? false : !loaded.get().disabled)
        }
        throw new IllegalArgumentException("unsupported type")
    }

    @Override
    String errorMessage() {
        return ERROR_MESSAGE
    }
}
