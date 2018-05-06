package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import com.jtbdevelopment.games.players.Player;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 11/4/2014 Time: 9:54 PM
 */
public abstract class AbstractHandler<ID extends Serializable, P extends Player<ID>> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
  private final AbstractPlayerRepository<ID, P> playerRepository;

  protected AbstractHandler(final AbstractPlayerRepository<ID, P> playerRepository) {
    this.playerRepository = playerRepository;
  }

  @SuppressWarnings("WeakerAccess")
  protected P loadPlayerMD5(final String md5) {
    P p = playerRepository.findByMd5(md5);
    if (p == null) {
      throw new FailedToFindPlayersException();
    }

    return p;
  }

  @SuppressWarnings("WeakerAccess")
  protected Set<P> loadPlayerMD5s(final Collection<String> playerMD5s) {
    List<P> loadedPlayers = playerRepository.findByMd5In(playerMD5s);
    Set<P> players = new HashSet<>(loadedPlayers);
    if (players.size() != playerMD5s.size()) {
      logger.info("Not all players were loaded " + playerMD5s + " vs. " + players);
      throw new FailedToFindPlayersException();
    }

    return players;
  }

  @SuppressWarnings("WeakerAccess")
  protected P loadPlayer(final ID playerID) {
    Optional<P> optional = playerRepository.findById(playerID);
    if (optional.isPresent()) {
      return optional.get();
    }

    logger.info("Player was not loaded " + playerID.toString());
    throw new FailedToFindPlayersException();
  }

}
