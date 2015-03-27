package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.games.MultiPlayerGame
import com.jtbdevelopment.games.games.masked.MultiPlayerGameMasker
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 11/17/14
 * Time: 6:37 AM
 */
@Component
@CompileStatic
class GameGetterHandler<ID extends Serializable> extends AbstractGameGetterHandler<ID> {
    @Autowired(required = false)
    protected MultiPlayerGameMasker gameMasker

    Game getGame(final ID playerID, final ID gameID) {
        Player player = loadPlayer(playerID)
        Game game = loadGame(gameID)
        validatePlayerForGame(game, player)
        if (game instanceof MultiPlayerGame && gameMasker) {
            return gameMasker.maskGameForPlayer(game, player)
        } else {
            return game
        }
    }
}
