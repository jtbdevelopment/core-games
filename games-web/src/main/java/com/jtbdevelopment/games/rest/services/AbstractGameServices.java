package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.handlers.DeclineRematchOptionHandler;
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import java.io.Serializable;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Date: 11/11/14 Time: 9:42 PM
 */
public abstract class AbstractGameServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>> {

  private final GameGetterHandler<ID, FEATURES, IMPL, M, P> gameGetterHandler;
  private final DeclineRematchOptionHandler<ID, FEATURES, IMPL, M, P> declineRematchOptionHandler;
  private ThreadLocal<ID> playerID = new ThreadLocal<>();
  private ThreadLocal<ID> gameID = new ThreadLocal<>();

  @SuppressWarnings("WeakerAccess")
  protected AbstractGameServices(
      final GameGetterHandler<ID, FEATURES, IMPL, M, P> gameGetterHandler,
      final DeclineRematchOptionHandler<ID, FEATURES, IMPL, M, P> declineRematchOptionHandler) {
    this.gameGetterHandler = gameGetterHandler;
    this.declineRematchOptionHandler = declineRematchOptionHandler;
  }

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

  public ThreadLocal<ID> getPlayerID() {
    return playerID;
  }

  public ThreadLocal<ID> getGameID() {
    return gameID;
  }
}
