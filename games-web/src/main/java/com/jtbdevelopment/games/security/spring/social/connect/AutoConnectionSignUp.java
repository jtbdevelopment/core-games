package com.jtbdevelopment.games.security.spring.social.connect;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * Date: 12/14/14 Time: 5:11 PM
 */
@Component
public class AutoConnectionSignUp implements ConnectionSignUp {

  private static final Logger logger = LoggerFactory.getLogger(AutoConnectionSignUp.class);
  private final AbstractPlayerRepository playerRepository;
  private final PlayerFactory playerFactory;

  public AutoConnectionSignUp(
      final AbstractPlayerRepository playerRepository,
      final PlayerFactory playerFactory) {
    this.playerRepository = playerRepository;
    this.playerFactory = playerFactory;
  }

  @Override
  public String execute(final Connection<?> connection) {
    try {
      Player player = playerRepository.findBySourceAndSourceId(connection.getKey().getProviderId(),
          connection.getKey().getProviderUserId());
      if (player != null) {
        return player.getIdAsString();
      } else {
        AbstractPlayer p = playerFactory.newPlayer();
        p.setDisabled(false);
        p.setDisplayName(connection.fetchUserProfile().getName());
        p.setSource(connection.getKey().getProviderId());
        p.setSourceId(connection.getKey().getProviderUserId());
        p.setProfileUrl(connection.getProfileUrl());
        p.setImageUrl(connection.getImageUrl());
        p = playerRepository.save(p);
        return (p == null ? null : p.getIdAsString());
      }

    } catch (Exception e) {
      logger.warn("Experienced exception in AutoConnectionSignUp", e);
      return null;
    }

  }
}
