package com.jtbdevelopment.games.security.spring.userdetails;

import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.security.spring.LastLoginUpdater;
import com.jtbdevelopment.games.security.spring.PlayerUserDetails;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 12/24/14 Time: 4:48 PM
 */
public class PlayerUserDetailsServiceTest {

  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private LastLoginUpdater lastLoginUpdater = Mockito.mock(LastLoginUpdater.class);
  private PlayerUserDetailsService userDetailsService = new PlayerUserDetailsService(
      playerRepository, lastLoginUpdater);

  @Test
  public void testLoadUserByUsername() {
    Mockito.when(
        playerRepository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, PTWO.getSourceId()))
        .thenReturn(PTWO);
    Player updated = Mockito.mock(Player.class);
    Mockito.when(lastLoginUpdater.updatePlayerLastLogin(PTWO)).thenReturn(updated);

    PlayerUserDetails d = (PlayerUserDetails) userDetailsService
        .loadUserByUsername(PTWO.getSourceId());
    Assert.assertSame(updated, d.getEffectiveUser());
    Assert.assertSame(updated, d.getSessionUser());
  }

  @Test
  public void testNoLoadUserByUsername() {
    Mockito.when(
        playerRepository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, PTWO.getSourceId()))
        .thenReturn(null);

    Assert.assertNull(userDetailsService.loadUserByUsername(PTWO.getSourceId()));
  }
}
