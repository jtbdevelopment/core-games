package com.jtbdevelopment.games.security.spring;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 8/16/2015 Time: 8:12 PM
 */
public class LastLoginUpdaterTest {

  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private LastLoginUpdater updater = new LastLoginUpdater(playerRepository);
  private Instant now = Instant.now();

  @Test
  public void testUpdatesPlayerIfLastLoginIsNull() {
    StringPlayer player = GameCoreTestCase.makeSimplePlayer("new");
    player.setLastLogin(null);
    StringPlayer saved = GameCoreTestCase.makeSimplePlayer("saved");
    saved.setLastLogin(null);

    Mockito.when(playerRepository.save(player)).thenReturn(saved);
    Assert.assertSame(saved, updater.updatePlayerLastLogin(player));

    Assert.assertNotNull(player.getLastLogin());
    Assert.assertTrue(now.compareTo(player.getLastLogin()) <= 0);
  }

  @Test
  public void testUpdatesPlayerIfLastLoginIsOlderThan15Minutes() {
    StringPlayer player = GameCoreTestCase.makeSimplePlayer("new");
    player.setLastLogin(now.minusSeconds(16 * 60));
    StringPlayer saved = GameCoreTestCase.makeSimplePlayer("saved");
    saved.setLastLogin(null);

    Mockito.when(playerRepository.save(player)).thenReturn(saved);
    Assert.assertSame(saved, updater.updatePlayerLastLogin(player));

    Assert.assertNotNull(player.getLastLogin());
    Assert.assertTrue(now.compareTo(player.getLastLogin()) <= 0);
  }

  @Test
  public void testDoesNotUpdatePlayerIfLastLoginIs15Minutes() {
    StringPlayer player = GameCoreTestCase.makeSimplePlayer("new");
    Instant originalLL = now.minusSeconds(14 * 60);
    player.setLastLogin(originalLL);

    Assert.assertSame(player, updater.updatePlayerLastLogin(player));

    Assert.assertNotNull(player.getLastLogin());
    Assert.assertEquals(originalLL, player.getLastLogin());
  }
}
