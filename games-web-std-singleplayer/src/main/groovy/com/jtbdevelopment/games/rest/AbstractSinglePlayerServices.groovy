package com.jtbdevelopment.games.rest

import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 4/8/2015
 * Time: 10:26 PM
 */
@CompileStatic
abstract class AbstractSinglePlayerServices<ID extends Serializable> extends AbstractPlayerServices<ID> {
    @Autowired
    PlayerGamesFinderHandler playerGamesFinderHandler

    @GET
    @Path("games")
    @Produces(MediaType.APPLICATION_JSON)
    List gamesForPlayer() {
        playerGamesFinderHandler.findGames(playerID.get())
    }
}
