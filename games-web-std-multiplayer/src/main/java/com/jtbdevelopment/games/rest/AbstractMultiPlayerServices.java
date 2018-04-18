package com.jtbdevelopment.games.rest;

import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler;
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices;
import java.io.Serializable;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 4/8/2015
 * Time: 10:26 PM
 */
public abstract class AbstractMultiPlayerServices<ID extends Serializable>
    extends AbstractPlayerServices<ID> {

  @Autowired
  protected PlayerGamesFinderHandler playerGamesFinderHandler;

  @GET
  @Path("games")
  @Produces(MediaType.APPLICATION_JSON)
  public List gamesForPlayer() {
    return playerGamesFinderHandler.findGames(getPlayerID().get());
  }

}
