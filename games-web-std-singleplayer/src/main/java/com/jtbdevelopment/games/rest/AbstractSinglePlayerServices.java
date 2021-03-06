package com.jtbdevelopment.games.rest;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler;
import com.jtbdevelopment.games.rest.services.AbstractAdminServices;
import com.jtbdevelopment.games.rest.services.AbstractGameServices;
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame;
import java.io.Serializable;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Date: 4/8/2015 Time: 10:26 PM
 */
public abstract class AbstractSinglePlayerServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractSinglePlayerGame<ID, FEATURES>,
    M extends AbstractMaskedSinglePlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractPlayerServices<ID, FEATURES, IMPL, M, P> {

  private final PlayerGamesFinderHandler<ID, FEATURES, IMPL, M, P> playerGamesFinderHandler;

  @SuppressWarnings("WeakerAccess")
  protected AbstractSinglePlayerServices(
      final AbstractGameServices<ID, FEATURES, IMPL, M, P> gamePlayServices,
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractAdminServices<ID, FEATURES, IMPL, P> adminServices,
      final StringToIDConverter<ID> stringToIDConverter,
      final PlayerGamesFinderHandler<ID, FEATURES, IMPL, M, P> playerGamesFinderHandler) {
    super(gamePlayServices, playerRepository, adminServices, stringToIDConverter);
    this.playerGamesFinderHandler = playerGamesFinderHandler;
  }

  @GET
  @Path("games")
  @Produces(MediaType.APPLICATION_JSON)
  public List gamesForPlayer() {
    return playerGamesFinderHandler.findGames(getPlayerID().get());
  }
}
