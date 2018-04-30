package com.jtbdevelopment.games.security.spring;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 8/16/2015 Time: 8:02 PM
 */
@Component
public class LastLoginUpdater {

  private static final int THRESHOLD = 15;

  private final AbstractPlayerRepository playerRepository;

  public LastLoginUpdater(final AbstractPlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public Player updatePlayerLastLogin(final Player player) {
    Instant now = Instant.now();
    if (player.getLastLogin() == null
        || Duration.between(player.getLastLogin(), now).toMinutes() > THRESHOLD) {
      player.setLastLogin(now);
      return playerRepository.save(player);
    }

    return player;
  }
}
