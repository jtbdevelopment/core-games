package com.jtbdevelopment.games.rest;

import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler;
import com.jtbdevelopment.games.rest.services.AbstractPlayerServices;
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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 4/8/2015 Time: 10:26 PM
 */
public abstract class AbstractMultiPlayerServices<ID extends Serializable>
    extends AbstractPlayerServices<ID> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractMultiPlayerServices.class);

  @Autowired
  protected PlayerGamesFinderHandler playerGamesFinderHandler;

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
