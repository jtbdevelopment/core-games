package com.jtbdevelopment.games.players;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.annotation.Transient;

/**
 * Date: 1/11/15 Time: 8:38 AM
 */
public class AbstractPlayerTest {

  @Test
  public void testInitializesDefaults() throws InterruptedException {
    Instant start = Instant.now();
    Thread.sleep(100);
    Player p = new StringPlayer();
    assertFalse(p.getDisabled());
    assertFalse(p.getAdminUser());
    assertTrue(start.compareTo(p.getCreated()) < 0);
    assertTrue(
        start.minusSeconds(365 * 24 * 60 * 60).compareTo(p.getLastLogin()) < 0);
    assertTrue(p.getLastLogin().compareTo(Instant.now()) <= 0);
    assertTrue(p.getCreated().compareTo(Instant.now()) <= 0);
    assertTrue(((StringPlayer) p).getRegisteredDevices().isEmpty());
  }

  @Test
  public void testHashCodeWithNoId() {
    Player p = new StringPlayer();
    assertEquals(p.hashCode(), 0);
  }

  @Test
  public void testHashCodeWithId() {
    assertEquals(GameCoreTestCase.PONE.hashCode(), GameCoreTestCase.PONE.getId().hashCode());
  }

  @Test
  public void testIdAsStringWithNoId() {
    Player p = new StringPlayer();
    assertNull(p.getIdAsString());
  }

  @Test
  public void testSourceCannotBeChanged() {
    String SOURCE = "SOURCE";
    StringPlayer player = new StringPlayer();

    player.setSource(SOURCE);
    assertEquals(SOURCE, player.getSource());
    player.setSource(SOURCE + "X");
    assertEquals(SOURCE, player.getSource());
  }

  @Test
  public void testMd5IsCombinationOfCorrectFields() {
    String key =
        PONE.getIdAsString() + PONE.getSource() + PONE.getDisplayName() + PONE.getSourceId();
    String md5 = DigestUtils.md5Hex(key);
    assertEquals(PONE.getMd5(), md5);
  }

  @Test
  public void testMd5IsBlankUntilAllFieldSet() {
    Player p = new StringPlayer();
    assertEquals("", p.getMd5());
    ((StringPlayer) p).setId("X");
    assertEquals("", p.getMd5());
    p.setDisplayName("Y");
    assertEquals("", p.getMd5());
    p.setSource("S");
    assertEquals("", p.getMd5());
    p.setSourceId("ID");
    assertEquals(DigestUtils.md5Hex("XSYID"), p.getMd5());
  }

  @Test
  public void testEquals() {
    assertEquals(PONE, PONE);
    Assert.assertNotEquals(PTWO, PONE);
    StringPlayer player = new StringPlayer();
    player.setId(PONE.getId());
    assertEquals(player, PONE);
    Assert.assertNotEquals("String", PONE);
    Assert.assertNotEquals(PONE, null);
  }

  @Test
  public void testToString() {
    StringPlayer player = new StringPlayer();
    player.setId("XYZ");
    player.setDisabled(false);
    player.setDisplayName("BAYMAX");
    player.setSourceId("BAYMAX");
    player.setSource("BIG HERO 6");
    assertEquals(
        "Player{id='XYZ', source='BIG HERO 6', sourceId='BAYMAX', displayName='BAYMAX', disabled=false}",
        player.toString());
  }

  @Test
  public void testSourceAndSourceIdString() {
    StringPlayer player = new StringPlayer();
    player.setId("XYZ");
    player.setDisabled(false);
    player.setDisplayName("BAYMAX");
    player.setSourceId("BAYMAX");
    player.setSource("BIG HERO 6");
    assertEquals("BIG HERO 6/BAYMAX", player.getSourceAndSourceId());
  }

  @Test
  public void testSourceAndSourceIdStringWhenOneIsNull() {
    StringPlayer player = new StringPlayer();
    player.setId("XYZ");
    player.setDisabled(false);
    player.setDisplayName("BAYMAX");
    player.setSourceId(null);
    player.setSource("BIG HERO 6");
    assertNull(player.getSourceAndSourceId());

    StringPlayer player1 = new StringPlayer();
    player1.setId("XYZ");
    player1.setDisabled(false);
    player1.setDisplayName("BAYMAX");
    player1.setSourceId("BAYMAX");
    player1.setSource(null);
    assertNull(player1.getSourceAndSourceId());

  }

  @Test
  public void testSettingGameSpecificAttributeAlsoLinksAttributeBackToPlayer() {
    TestPlayerAttributes attributes = new TestPlayerAttributes();

    attributes.setSomeAttribute(5);

    assertNull(attributes.getPlayer());

    StringPlayer player = new StringPlayer();

    player.setGameSpecificPlayerAttributes(attributes);
    assertSame(player, attributes.getPlayer());
  }

  @Test
  public void testSettingGameSpecificAttributeToNullDoesNotException() {
    Player p = new StringPlayer();
    p.setGameSpecificPlayerAttributes(null);
    assertNull(((StringPlayer) p).getGameSpecificPlayerAttributes());
  }

  @Test
  public void testUpdatingDeviceNotInSetAlready() {
    Player p = new StringPlayer();
    RegisteredDevice device = new RegisteredDevice();

    device.setDeviceID("X");
    p.updateRegisteredDevice(device);
    assertEquals(new HashSet<>(Arrays.asList(device)), ((StringPlayer) p).getRegisteredDevices());
  }

  @Test
  public void testUpdatingExistingDevice() {
    Player p = new StringPlayer();
    RegisteredDevice device = new RegisteredDevice();

    device.setDeviceID("X");
    p.updateRegisteredDevice(device);

    RegisteredDevice updatedDevice = new RegisteredDevice();

    updatedDevice.setDeviceID(device.getDeviceID());
    updatedDevice.setLastRegistered(device.getLastRegistered().plusSeconds(1));

    p.updateRegisteredDevice(updatedDevice);

    assertEquals(new HashSet<>(Arrays.asList(updatedDevice)),
        ((StringPlayer) p).getRegisteredDevices());
    assertEquals(updatedDevice.getLastRegistered(),
        ((StringPlayer) p).getRegisteredDevices().iterator().next().getLastRegistered());
  }

  @Test
  public void testRemovingAnExistingDevice() {
    Player p = new StringPlayer();
    RegisteredDevice device = new RegisteredDevice();

    device.setDeviceID("X");
    device.setLastRegistered(Instant.now());
    p.updateRegisteredDevice(device);

    assertTrue(((StringPlayer) p).getRegisteredDevices().contains(device));

    RegisteredDevice remove = new RegisteredDevice();

    remove.setDeviceID("X");
    remove.setLastRegistered(Instant.now());
    p.removeRegisteredDevice(remove);
    assertFalse(((StringPlayer) p).getRegisteredDevices().contains(device));
  }

  private class TestPlayerAttributes implements GameSpecificPlayerAttributes {

    @Transient
    private Player player;
    private int someAttribute;

    public Player getPlayer() {
      return player;
    }

    public void setPlayer(Player player) {
      this.player = player;
    }

    public int getSomeAttribute() {
      return someAttribute;
    }

    public void setSomeAttribute(int someAttribute) {
      this.someAttribute = someAttribute;
    }
  }
}
