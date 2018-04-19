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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 11/4/2014 Time: 9:54 PM
 */
public abstract class AbstractHandler {

  private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
  @Autowired
  protected AbstractPlayerRepository playerRepository;

  protected Player loadPlayerMD5(final String md5) {
    Player p = playerRepository.findByMd5(md5);
    if (p == null) {
      throw new FailedToFindPlayersException();
    }

    return p;
  }

  protected Set<Player> loadPlayerMD5s(final Collection<String> playerMD5s) {
    List<? extends Player<? extends Serializable>> loadedPlayers = playerRepository
        .findByMd5In(playerMD5s);
    HashSet<Player> players = new HashSet<>(loadedPlayers);
    if (players.size() != playerMD5s.size()) {
      logger.info("Not all players were loaded " + playerMD5s + " vs. " + players);
      throw new FailedToFindPlayersException();
    }

    return players;
  }

  protected Player loadPlayer(final Serializable playerID) {
    Optional<? extends Player<? extends Serializable>> optional = playerRepository
        .findById(playerID);
    if (optional.isPresent()) {
      return optional.get();
    }

    logger.info("Player was not loaded " + playerID.toString());
    throw new FailedToFindPlayersException();
  }

}
