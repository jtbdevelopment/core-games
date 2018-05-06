package com.jtbdevelopment.games.security.spring;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 8/16/2015 Time: 8:02 PM
 */
@Component
public class LastLoginUpdater<ID extends Serializable, P extends AbstractPlayer<ID>> {

  private static final int THRESHOLD = 15;

  private final AbstractPlayerRepository<ID, P> playerRepository;

  public LastLoginUpdater(final AbstractPlayerRepository<ID, P> playerRepository) {
    this.playerRepository = playerRepository;
  }

  public P updatePlayerLastLogin(final P player) {
    Instant now = Instant.now();
    if (player.getLastLogin() == null
        || Duration.between(player.getLastLogin(), now).toMinutes() > THRESHOLD) {
      player.setLastLogin(now);
      return playerRepository.save(player);
    }

    return player;
  }
}
