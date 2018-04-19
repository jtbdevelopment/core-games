package com.jtbdevelopment.games.push.rest;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.jtbdevelopment.games.push.PushProperties;
import com.jtbdevelopment.games.security.SessionUserInfo;
import java.io.Serializable;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Date: 10/16/15 Time: 6:44 AM
 */
@Component
@Path("notifications")
@RolesAllowed(PlayerRoles.PLAYER)
public class PushServices {

  @Autowired
  protected PushProperties pushProperties;
  @Autowired
  protected AbstractPlayerRepository playerRepository;

  @GET
  @Path("senderID")
  @Produces(MediaType.TEXT_PLAIN)
  public String senderID() {
    return pushProperties.getSenderID();
  }

  @PUT
  @Path("register/{deviceID}")
  @Produces(MediaType.APPLICATION_JSON)
  public Object registerDevice(@PathParam("deviceID") final String deviceID) {

    //  TODO - remove old devices here?

    //  TODO - register client /update client in GCM

    Player player = getPlayer();

    RegisteredDevice device = makeDevice(deviceID);
    player.updateRegisteredDevice(device);
    return playerRepository.save(player);
  }

  @PUT
  @Path("unregister/{deviceID}")
  @Produces(MediaType.APPLICATION_JSON)
  public Object unregisteredDevice(@PathParam("deviceID") final String deviceID) {
    Player player = getPlayer();

    //  TODO - register client /update client in GCM

    RegisteredDevice device = makeDevice(deviceID);
    player.removeRegisteredDevice(device);
    return playerRepository.save(player);
  }

  private RegisteredDevice makeDevice(final String deviceID) {
    RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID(deviceID);
    return device;
  }

  private Player getPlayer() {
    Serializable id = ((SessionUserInfo<Serializable>) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal()).getEffectiveUser().getId();
    return (Player) playerRepository.findById(id).get();
  }

}
