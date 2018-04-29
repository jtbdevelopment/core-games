package com.jtbdevelopment.games.security.spring.social.security;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.security.spring.LastLoginUpdater;
import com.jtbdevelopment.games.security.spring.PlayerUserDetails;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import com.jtbdevelopment.games.stringimpl.StringToStringConverter;
import java.util.Optional;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.security.SocialUserDetails;

/**
 * Date: 1/7/15 Time: 6:51 PM
 */
public class PlayerSocialUserDetailsServiceTest {

  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private LastLoginUpdater lastLoginUpdater = Mockito.mock(LastLoginUpdater.class);
  private PlayerSocialUserDetailsService service = new PlayerSocialUserDetailsService(
      playerRepository, new StringToStringConverter(), lastLoginUpdater);

  @Test
  public void testReturnsWrappedPlayerIfFoundAfterCallingLoginUpdated() {
    Player player = GameCoreTestCase.makeSimplePlayer("4524");
    Player playerCopy = GameCoreTestCase.makeSimplePlayer("4524");
    when(playerRepository
        .findById(GameCoreTestCase.reverse(((StringPlayer) player).getIdAsString())))
        .thenReturn(Optional.of((StringPlayer) player));
    when(lastLoginUpdater.updatePlayerLastLogin(player)).thenReturn(playerCopy);

    SocialUserDetails userDetails = service.loadUserByUserId(((StringPlayer) player).getId());
    assertTrue(userDetails instanceof PlayerUserDetails);
    assertSame(playerCopy, ((PlayerUserDetails) userDetails).getSessionUser());
  }

  @Test
  public void testReturnsNullIfNotFound() {
    String id = "ANID";
    when(playerRepository.findById(GameCoreTestCase.reverse(id)))
        .thenReturn(Optional.empty());

    assertNull(service.loadUserByUserId(id));
  }
}
