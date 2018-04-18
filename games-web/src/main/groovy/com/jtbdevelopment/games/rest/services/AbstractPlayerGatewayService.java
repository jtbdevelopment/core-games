package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import com.jtbdevelopment.games.state.GamePhase;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Date: 11/14/14
 * Time: 6:36 AM
 */
@RolesAllowed({PlayerRoles.PLAYER})
public abstract class AbstractPlayerGatewayService<ID extends Serializable> {

  public static final String PING_RESULT = "Alive.";
  @Autowired
  protected AbstractPlayerServices playerServices;

  @Path("player")
  public Object gameServices() {
    playerServices.getPlayerID().set(
        ((SessionUserInfo<ID>) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal()).getEffectiveUser().getId());
    return playerServices;
  }

  @Produces(MediaType.TEXT_PLAIN)
  @GET
  @Path("ping")
  public String ping() {
    return PING_RESULT;
  }

  @GET
  @Path("phases")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<GamePhase, List<String>> phasesAndDescriptions() {
    return Arrays.stream(GamePhase.values())
        .collect(Collectors.toMap(
            p -> p,
            p -> Arrays.asList(p.getDescription(), p.getGroupLabel())));
  }
}
