package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.rest.handlers.GameGetterHandler
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.GET
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

    //  TODO - would like this moved as well
    @Autowired
    GameGetterHandler gameGetterHandler

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Object getGame() {
        gameGetterHandler.getGame(playerID.get(), gameID.get())
    }
}
