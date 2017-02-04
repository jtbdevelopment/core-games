package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.rest.handlers.ChallengeToRematchHandler
import com.jtbdevelopment.games.rest.handlers.DeclineRematchOptionHandler
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler
import com.jtbdevelopment.games.rest.handlers.QuitHandler
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 11/11/14
 * Time: 9:42 PM
 */
@CompileStatic
abstract class AbstractGameServices<ID extends Serializable> {
    ThreadLocal<ID> playerID = new ThreadLocal<>()
    ThreadLocal<ID> gameID = new ThreadLocal<>()

    @Autowired
    GameGetterHandler gameGetterHandler

    @Autowired
    QuitHandler quitHandler
    @Autowired
    ChallengeToRematchHandler rematchHandler
    @Autowired
    DeclineRematchOptionHandler declineRematchOptionHandler

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Object getGame() {
        gameGetterHandler.getGame(playerID.get(), gameID.get())
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("endRematch")
    Object endRematch() {
        declineRematchOptionHandler.handleAction(playerID.get(), gameID.get())
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rematch")
    Object createRematch() {
        rematchHandler.handleAction(playerID.get(), gameID.get())
    }

    @PUT
    @Path("quit")
    @Produces(MediaType.APPLICATION_JSON)
    Object quitGame() {
        quitHandler.handleAction(playerID.get(), gameID.get())
    }

}
