package com.jtbdevelopment.games.security.spring.userdetails;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.security.spring.LastLoginUpdater;
import com.jtbdevelopment.games.security.spring.PlayerUserDetails;
import java.io.Serializable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Date: 12/16/14 Time: 12:31 PM
 *
 * This is presumably only used by manual login where username = sourceId and source = MANUAL
 */
@Component
public class PlayerUserDetailsService<ID extends Serializable, P extends AbstractPlayer<ID>>
    implements UserDetailsService {

  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final LastLoginUpdater<ID, P> lastLoginUpdater;

  public PlayerUserDetailsService(
      @SuppressWarnings("SpringJavaAutowiringInspection") final AbstractPlayerRepository<ID, P> playerRepository,
      final LastLoginUpdater<ID, P> lastLoginUpdater) {
    this.playerRepository = playerRepository;
    this.lastLoginUpdater = lastLoginUpdater;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    P player = playerRepository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, username);
    if (player != null) {
      return new PlayerUserDetails<>(lastLoginUpdater.updatePlayerLastLogin(player));
    } else {
      return null;
    }

  }
}
