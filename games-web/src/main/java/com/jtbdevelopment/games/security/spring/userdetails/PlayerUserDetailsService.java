package com.jtbdevelopment.games.security.spring.userdetails;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.security.spring.LastLoginUpdater;
import com.jtbdevelopment.games.security.spring.PlayerUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PlayerUserDetailsService implements UserDetailsService {

  @Autowired
  protected AbstractPlayerRepository playerRepository;
  @Autowired
  protected LastLoginUpdater lastLoginUpdater;

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    Player player = playerRepository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, username);
    if (player != null) {
      return new PlayerUserDetails(lastLoginUpdater.updatePlayerLastLogin(player));
    } else {
      return null;
    }

  }
}
