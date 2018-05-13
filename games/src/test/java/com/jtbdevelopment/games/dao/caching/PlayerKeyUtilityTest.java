package com.jtbdevelopment.games.dao.caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;

/**
 * Date: 3/2/15 Time: 6:28 PM
 */
public class PlayerKeyUtilityTest {

  private AbstractPlayerRepository playerRepository = mock(AbstractPlayerRepository.class);
  private AbstractPlayer<Serializable> player = mock(AbstractPlayer.class);
  private AbstractPlayer<Serializable> player2 = mock(AbstractPlayer.class);
  private AbstractPlayer<Serializable> player3 = mock(AbstractPlayer.class);
  private PlayerKeyUtility playerKeyUtility = new PlayerKeyUtility(playerRepository);

  @Test
  public void testCollectPlayerIDs() {
    when(player.getId()).thenReturn("X");
    when(player2.getId()).thenReturn("1");
    when(player3.getId()).thenReturn("B3");
    assertEquals(
        Arrays.asList("X", "1", "B3"),
        PlayerKeyUtility.collectPlayerIDs(Arrays.asList(player, player2, player3)));
  }

  @Test
  public void testCollectPlayerIDsWithNull() {
    assertTrue(PlayerKeyUtility.collectPlayerIDs(null).isEmpty());
  }

  @Test
  public void testCollectPlayerMD5s() {
    when(player.getMd5()).thenReturn("X");
    when(player2.getMd5()).thenReturn("1");
    when(player3.getMd5()).thenReturn("B3");
    assertEquals(
        Arrays.asList("X", "1", "B3"),
        PlayerKeyUtility.collectPlayerMD5s(Arrays.asList(player, player2, player3)));
  }

  @Test
  public void testCollectPlayerMD5sWithNull() {
    assertTrue(PlayerKeyUtility.collectPlayerMD5s(null).isEmpty());
  }

  @Test
  public void testCollectPlayerSourceAndSourceIDs() {
    when(player.getSourceAndSourceId()).thenReturn("S1/X");
    when(player2.getSourceAndSourceId()).thenReturn("S2/1");
    when(player3.getSourceAndSourceId()).thenReturn("S3/B3");
    assertEquals(
        Arrays.asList("S1/X", "S2/1", "S3/B3"),
        PlayerKeyUtility.collectPlayerSourceAndSourceIDs(Arrays.asList(player, player2, player3)));
  }

  @Test
  public void testCollectPlayerSourceAndSourceIDsWithNull() {
    assertTrue(PlayerKeyUtility.collectPlayerSourceAndSourceIDs(null).isEmpty());
  }

  @Test
  public void testCollectSourceAndSourceIDs() {
    assertEquals(
        Arrays.asList("S1/X", "S1/Y", "S1/Z"),
        PlayerKeyUtility.collectSourceAndSourceIDs("S1", Arrays.asList("X", "Y", "Z")));
  }

  @Test
  public void testCollectSourceAndSourceIDsWithNull() {
    assertTrue(PlayerKeyUtility.collectSourceAndSourceIDs(null, null).isEmpty());
    assertTrue(PlayerKeyUtility.collectSourceAndSourceIDs("X", null).isEmpty());
    assertTrue(
        PlayerKeyUtility.collectSourceAndSourceIDs(null, Collections.singletonList("X")).isEmpty());
  }

  @Test
  public void testMd5FromID() {
    String ID = "ANID";
    String md5 = "MD5";
    when(player.getMd5()).thenReturn(md5);
    when(playerRepository.findById(ID)).thenReturn(Optional.of(player));
    assertEquals(PlayerKeyUtility.md5FromID(ID), md5);
  }

  @Test
  public void testMd5FromIDNullResult() {
    String ID = "ANID";
    when(playerRepository.findById(ID)).thenReturn(Optional.empty());
    assertNull(PlayerKeyUtility.md5FromID(ID));
  }

  @Test
  public void testSourceAndSourceIDFromID() {
    String ID = "ANID";
    String ssid = "XSID";
    when(player.getSourceAndSourceId()).thenReturn(ssid);
    when(playerRepository.findById(ID)).thenReturn(Optional.of(player));
    assertEquals(PlayerKeyUtility.sourceAndSourceIDFromID(ID), ssid);
  }

  @Test
  public void testSourceAndSourceIDFromIDWithNull() {
    String ID = "ANID";
    when(playerRepository.findById(ID)).thenReturn(Optional.empty());
    assertNull(PlayerKeyUtility.sourceAndSourceIDFromID(ID));
  }
}
