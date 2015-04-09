package com.jtbdevelopment.games.rest

import com.jtbdevelopment.games.rest.handlers.AbstractChallengeToRematchHandler
import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler
import com.jtbdevelopment.games.rest.handlers.QuitHandler
import com.jtbdevelopment.games.rest.services.AbstractGameServices
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 4/8/2015
 * Time: 10:26 PM
 */
@CompileStatic
abstract class AbstractMultiPlayerGameServices<ID extends Serializable> extends AbstractGameServices<ID> {
    @Autowired
    QuitHandler quitHandler
    @Autowired
    ChallengeResponseHandler responseHandler
    @Autowired
    AbstractChallengeToRematchHandler rematchHandler

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rematch")
    Object createRematch() {
        rematchHandler.handleAction(playerID.get(), gameID.get())
    }

    @PUT
    @Path("reject")
    @Produces(MediaType.APPLICATION_JSON)
    Object rejectGame() {
        responseHandler.handleAction(playerID.get(), gameID.get(), PlayerState.Rejected)
    }

    @PUT
    @Path("accept")
    @Produces(MediaType.APPLICATION_JSON)
    Object acceptGame() {
        responseHandler.handleAction(playerID.get(), gameID.get(), PlayerState.Accepted)
    }

    @PUT
    @Path("quit")
    @Produces(MediaType.APPLICATION_JSON)
    Object quitGame() {
        quitHandler.handleAction(playerID.get(), gameID.get())
    }

}
