package com.jtbdevelopment.games.rest;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler;
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;
import com.jtbdevelopment.games.state.masking.MaskedGame;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 4/8/2015 Time: 10:26 PM
 */
public abstract class AbstractMultiPlayerServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    M extends AbstractMaskedMultiPlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractPlayerServices<ID, FEATURES, IMPL, M, P> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractMultiPlayerServices.class);

  private PlayerGamesFinderHandler<ID, FEATURES, IMPL, M, P> playerGamesFinderHandler;

  @SuppressWarnings("WeakerAccess")
  protected AbstractMultiPlayerServices(
      final PlayerGamesFinderHandler<ID, FEATURES, IMPL, M, P> playerGamesFinderHandler) {
    this.playerGamesFinderHandler = playerGamesFinderHandler;
  }

  @GET
  @Path("games")
  @Produces(MediaType.APPLICATION_JSON)
  public List gamesForPlayer() {
    List<MaskedGame> games = playerGamesFinderHandler.findGames(getPlayerID().get());
    logger.info("Found {} games", games.size());
    logger.info("IDS {}",
        games.stream().map(game -> game.getIdAsString() + "/" + game.getGamePhase())
            .collect(Collectors.joining(",")));
    return games;
  }

}
