package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.rest.handlers.ChallengeToRematchHandler;
import com.jtbdevelopment.games.rest.handlers.DeclineRematchOptionHandler;
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler;
import com.jtbdevelopment.games.rest.handlers.QuitHandler;
import java.io.Serializable;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 11/11/14
 * Time: 9:42 PM
 */
public abstract class AbstractGameServices<ID extends Serializable> {

  @Autowired
  protected GameGetterHandler gameGetterHandler;
  @Autowired
  protected QuitHandler quitHandler;
  @Autowired
  protected ChallengeToRematchHandler rematchHandler;
  @Autowired
  protected DeclineRematchOptionHandler declineRematchOptionHandler;
  private ThreadLocal<ID> playerID = new ThreadLocal<>();
  private ThreadLocal<ID> gameID = new ThreadLocal<>();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Object getGame() {
    return gameGetterHandler.getGame(playerID.get(), gameID.get());
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("endRematch")
  public Object endRematch() {
    return declineRematchOptionHandler.handleAction(playerID.get(), gameID.get());
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("rematch")
  public Object createRematch() {
    return rematchHandler.handleAction(playerID.get(), gameID.get());
  }

  @PUT
  @Path("quit")
  @Produces(MediaType.APPLICATION_JSON)
  public Object quitGame() {
    return quitHandler.handleAction(playerID.get(), gameID.get());
  }

  public ThreadLocal<ID> getPlayerID() {
    return playerID;
  }

  public ThreadLocal<ID> getGameID() {
    return gameID;
  }
}
