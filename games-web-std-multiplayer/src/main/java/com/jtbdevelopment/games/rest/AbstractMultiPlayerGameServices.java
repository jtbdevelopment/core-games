package com.jtbdevelopment.games.rest;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler;
import com.jtbdevelopment.games.rest.handlers.ChallengeToRematchHandler;
import com.jtbdevelopment.games.rest.handlers.DeclineRematchOptionHandler;
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler;
import com.jtbdevelopment.games.rest.handlers.QuitHandler;
import com.jtbdevelopment.games.rest.services.AbstractGameServices;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;
import java.io.Serializable;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Date: 4/8/2015 Time: 10:26 PM
 */
public abstract class AbstractMultiPlayerGameServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    M extends AbstractMaskedMultiPlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameServices<ID, FEATURES, IMPL, M, P> {

  private final ChallengeResponseHandler<ID, FEATURES, IMPL, M, P> responseHandler;
  private final ChallengeToRematchHandler<ID, FEATURES, IMPL, M, P> rematchHandler;
  private final QuitHandler<ID, FEATURES, IMPL, M, P> quitHandler;

  @SuppressWarnings("WeakerAccess")
  protected AbstractMultiPlayerGameServices(
      final GameGetterHandler<ID, FEATURES, IMPL, M, P> gameGetterHandler,
      final DeclineRematchOptionHandler<ID, FEATURES, IMPL, M, P> declineRematchOptionHandler,
      final ChallengeResponseHandler<ID, FEATURES, IMPL, M, P> responseHandler,
      final ChallengeToRematchHandler<ID, FEATURES, IMPL, M, P> rematchHandler,
      final QuitHandler<ID, FEATURES, IMPL, M, P> quitHandler) {
    super(gameGetterHandler, declineRematchOptionHandler);
    this.responseHandler = responseHandler;
    this.rematchHandler = rematchHandler;
    this.quitHandler = quitHandler;
  }

  @PUT
  @Path("reject")
  @Produces(MediaType.APPLICATION_JSON)
  public Object rejectGame() {
    return responseHandler
        .handleAction(getPlayerID().get(), getGameID().get(), PlayerState.Rejected);
  }

  @PUT
  @Path("accept")
  @Produces(MediaType.APPLICATION_JSON)
  public Object acceptGame() {
    return responseHandler
        .handleAction(getPlayerID().get(), getGameID().get(), PlayerState.Accepted);
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Path("rematch")
  public Object createRematch() {
    return rematchHandler.handleAction(getPlayerID().get(), getGameID().get());
  }

  @PUT
  @Path("quit")
  @Produces(MediaType.APPLICATION_JSON)
  public Object quitGame() {
    return quitHandler.handleAction(getPlayerID().get(), getGameID().get());
  }

}
