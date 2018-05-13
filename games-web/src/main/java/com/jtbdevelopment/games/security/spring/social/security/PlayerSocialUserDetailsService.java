package com.jtbdevelopment.games.security.spring.social.security;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.security.spring.LastLoginUpdater;
import com.jtbdevelopment.games.security.spring.PlayerUserDetails;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Date: 12/13/14 Time: 9:36 PM
 */
@Component
public class PlayerSocialUserDetailsService<ID extends Serializable, P extends AbstractPlayer<ID>>
    implements SocialUserDetailsService {

  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final StringToIDConverter<ID> stringToIDConverter;
  private final LastLoginUpdater<ID, P> lastLoginUpdater;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  public PlayerSocialUserDetailsService(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final StringToIDConverter<ID> stringToIDConverter,
      final LastLoginUpdater<ID, P> lastLoginUpdater) {
    this.playerRepository = playerRepository;
    this.stringToIDConverter = stringToIDConverter;
    this.lastLoginUpdater = lastLoginUpdater;
  }

  @Override
  public SocialUserDetails loadUserByUserId(final String userId)
      throws UsernameNotFoundException, DataAccessException {
    //noinspection ConstantConditions
    Optional<P> optional = playerRepository.findById(stringToIDConverter.convert(userId)
    );
    return optional.<SocialUserDetails>map(
        p -> new PlayerUserDetails<>(lastLoginUpdater.updatePlayerLastLogin(p))).orElse(null);

  }
}
