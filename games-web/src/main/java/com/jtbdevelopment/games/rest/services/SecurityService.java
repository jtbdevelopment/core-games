package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Date: 12/14/14 Time: 7:54 PM
 */
@Path("security")
@RolesAllowed(PlayerRoles.PLAYER)
@Component
public class SecurityService {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Object getSessionPlayer() {
    return ((SessionUserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getSessionUser();
  }

}
