package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.masking.GameMasker
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 11/17/14
 * Time: 6:37 AM
 */
@Component
@CompileStatic
class GameGetterHandler extends AbstractGameGetterHandler {
    @Autowired(required = false)
    GameMasker gameMasker

    Game getGame(final Serializable playerID, final Serializable gameID) {
        Player player = loadPlayer(playerID)
        Game game = loadGame(gameID)
        validatePlayerForGame(game, player)
        if (gameMasker) {
            return gameMasker.maskGameForPlayer(game, player)
        } else {
            return game
        }
    }
}
