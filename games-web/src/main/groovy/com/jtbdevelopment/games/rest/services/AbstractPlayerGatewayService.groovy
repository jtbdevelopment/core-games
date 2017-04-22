package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.security.SessionUserInfo
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 11/14/14
 * Time: 6:36 AM
 */
@CompileStatic
@RolesAllowed([PlayerRoles.PLAYER])
abstract class AbstractPlayerGatewayService<ID extends Serializable> {
    public static final String PING_RESULT = "Alive."

    @Autowired
    AbstractPlayerServices playerServices

    @Path("player")
    Object gameServices() {
        playerServices.playerID.set(((SessionUserInfo<ID>) SecurityContextHolder.context.authentication.principal).effectiveUser.id)
        return playerServices
    }

    @Produces(MediaType.TEXT_PLAIN)
    @GET
    @Path("ping")
    @SuppressWarnings("GrMethodMayBeStatic")
    String ping() {
        return PING_RESULT
    }

    @GET
    @Path("phases")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("GrMethodMayBeStatic")
    Map<GamePhase, List<String>> phasesAndDescriptions() {
        GamePhase.values().collectEntries() {
            GamePhase it ->
                [(it): [it.description, it.groupLabel]]
        }
    }
}
