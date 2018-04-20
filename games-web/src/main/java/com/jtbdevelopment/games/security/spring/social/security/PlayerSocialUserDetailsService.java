package com.jtbdevelopment.games.security.spring.social.security;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.security.spring.LastLoginUpdater;
import com.jtbdevelopment.games.security.spring.PlayerUserDetails;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Date: 12/13/14 Time: 9:36 PM
 */
@Component
public class PlayerSocialUserDetailsService implements SocialUserDetailsService {

  @Autowired
  protected AbstractPlayerRepository playerRepository;
  @Autowired
  protected StringToIDConverter<? extends Serializable> stringToIDConverter;
  @Autowired
  protected LastLoginUpdater lastLoginUpdater;

  @Override
  public SocialUserDetails loadUserByUserId(final String userId)
      throws UsernameNotFoundException, DataAccessException {
    Optional<? extends Player> optional = playerRepository
        .findById(stringToIDConverter.convert(userId));
    if (optional.isPresent()) {
      return new PlayerUserDetails(lastLoginUpdater.updatePlayerLastLogin(optional.get()));
    }

    return null;
  }
}
