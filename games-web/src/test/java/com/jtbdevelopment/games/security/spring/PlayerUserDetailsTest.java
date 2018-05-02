package com.jtbdevelopment.games.security.spring;

import static com.jtbdevelopment.games.GameCoreTestCase.PINACTIVE1;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.PlayerRoles;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Date: 12/24/14 Time: 3:06 PM
 */
public class PlayerUserDetailsTest {

  private static ManualPlayer adminPlayer = GameCoreTestCase
      .makeSimpleManualPlayer("M1", "YAR!", true, false, true);
  private static ManualPlayer manualPlayer = GameCoreTestCase
      .makeSimpleManualPlayer("M2", "YAR!", true, false, false);
  private static ManualPlayer nonVerifiedPlayer = GameCoreTestCase
      .makeSimpleManualPlayer("M2", "YAR!", false, false, false);

  @Test
  public void testGetSessionUser() {
    PlayerUserDetails d = new PlayerUserDetails(PONE);
    Assert.assertSame(PONE, d.getSessionUser());
  }

  @Test
  public void testGetEffectiveUser() {
    PlayerUserDetails d = new PlayerUserDetails(PONE);
    Assert.assertSame(PONE, d.getEffectiveUser());
  }

  @Test
  public void testSetEffectiveUser() {
    PlayerUserDetails d = new PlayerUserDetails(PONE);
    Assert.assertSame(PONE, d.getEffectiveUser());
    Assert.assertSame(PONE, d.getSessionUser());
    d.setEffectiveUser(PTWO);
    Assert.assertSame(PTWO, d.getEffectiveUser());
    Assert.assertSame(PONE, d.getSessionUser());
  }

  @Test
  public void testGetUserId() {
    PlayerUserDetails d = new PlayerUserDetails(PONE);
    Assert.assertEquals(PONE.getIdAsString(), d.getUserId());
    d.setEffectiveUser(PTWO);
    Assert.assertEquals(PONE.getIdAsString(), d.getUserId());
  }

  @Test
  public void testGetAuthorities() {
    Assert.assertEquals(new ArrayList<SimpleGrantedAuthority>(
            Arrays.asList(new SimpleGrantedAuthority(PlayerRoles.PLAYER))),
        new PlayerUserDetails(manualPlayer).getAuthorities());
    Assert.assertEquals(new ArrayList<SimpleGrantedAuthority>(Arrays
            .asList(new SimpleGrantedAuthority(PlayerRoles.PLAYER),
                new SimpleGrantedAuthority(PlayerRoles.ADMIN))),
        new PlayerUserDetails(adminPlayer).getAuthorities());
  }

  @Test
  public void testGetPassword() {
    Assert.assertNull(new PlayerUserDetails(PONE).getPassword());
    Assert.assertEquals(manualPlayer.getPassword(),
        new PlayerUserDetails(manualPlayer).getPassword());
  }

  @Test
  public void testGetUsername() {
    Assert.assertEquals(PONE.getId(), new PlayerUserDetails(PONE).getUsername());
    Assert.assertEquals(manualPlayer.getSourceId(),
        new PlayerUserDetails(manualPlayer).getUsername());
  }

  @Test
  public void testIsAccountNonExpired() {
    Assert.assertTrue(new PlayerUserDetails(null).isAccountNonExpired());
  }

  @Test
  public void testIsAccountNonLocked() {
    Assert.assertTrue(new PlayerUserDetails(PONE).isAccountNonLocked());
    Assert.assertTrue(new PlayerUserDetails(manualPlayer).isAccountNonLocked());
    Assert.assertFalse(new PlayerUserDetails(nonVerifiedPlayer).isAccountNonLocked());
  }

  @Test
  public void testIsCredentialsNonExpired() {
    Assert.assertTrue(new PlayerUserDetails(null).isCredentialsNonExpired());
  }

  @Test
  public void testIsEnabled() {
    Assert.assertTrue(new PlayerUserDetails(PONE).isEnabled());
    Assert.assertFalse(new PlayerUserDetails(PINACTIVE1).isEnabled());
  }
}
