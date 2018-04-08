package com.jtbdevelopment.games.push.rest

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.players.notifications.RegisteredDevice
import com.jtbdevelopment.games.push.PushProperties
import com.jtbdevelopment.games.security.SessionUserInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 10/16/15
 * Time: 6:44 AM
 */
@CompileStatic
@Component
@Path("notifications")
@RolesAllowed(PlayerRoles.PLAYER)
class PushServices {
    @Autowired
    PushProperties pushProperties

    @Autowired
    AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository

    @GET
    @Path("senderID")
    @Produces(MediaType.TEXT_PLAIN)
    String senderID() {
        return pushProperties.senderID
    }

    @PUT
    @Path("register/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    Object registerDevice(@PathParam("deviceID") final String deviceID) {

        //  TODO - remove old devices here?

        //  TODO - register client /update client in GCM

        Player player = playerRepository.findById(
                ((SessionUserInfo<Serializable>) SecurityContextHolder.context.authentication.principal).
                        effectiveUser.id).get()

        RegisteredDevice device = new RegisteredDevice(deviceID: deviceID)
        player.updateRegisteredDevice(device)
        playerRepository.save(player)
    }

    @PUT
    @Path("unregister/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    Object unregisteredDevice(@PathParam("deviceID") final String deviceID) {
        Player player = playerRepository.findById(
                ((SessionUserInfo<Serializable>) SecurityContextHolder.context.authentication.principal).
                        effectiveUser.id).get()

        //  TODO - register client /update client in GCM

        RegisteredDevice device = new RegisteredDevice(deviceID: deviceID)
        player.removeRegisteredDevice(device)
        playerRepository.save(player)
    }
}
