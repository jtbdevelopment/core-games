package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.TwistedHangman.game.handlers.*
import groovy.transform.CompileStatic

/**
 * Date: 11/11/14
 * Time: 9:42 PM
 */
@CompileStatic
abstract class AbstractGameServices<ID extends Serializable> {
    ThreadLocal<ID> playerID = new ThreadLocal<>()
    ThreadLocal<ID> gameID = new ThreadLocal<>()

    //  TODO - would like this moved as well
//    @Autowired
//    GameGetterHandler gameGetterHandler

    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    MaskedMultiPlayerGame getGame() {
        gameGetterHandler.getGame(playerID.get(), gameID.get())
    }
    */
}
