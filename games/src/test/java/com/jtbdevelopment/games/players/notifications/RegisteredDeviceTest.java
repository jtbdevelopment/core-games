package com.jtbdevelopment.games.players.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 10/16/15 Time: 6:54 AM
 */
public class RegisteredDeviceTest {

  private RegisteredDevice device1 = new RegisteredDevice();
  private RegisteredDevice device2 = new RegisteredDevice();
  private RegisteredDevice device3 = new RegisteredDevice();
  private RegisteredDevice device4 = new RegisteredDevice();

  @Before
  public void setup() {
    device1.setDeviceID("X12345");
    device3.setDeviceID("X12345");
    device2.setDeviceID(device1.getDeviceID());
    device4.setDeviceID("4hjx");
  }

  @Test
  public void testConstructorDevice() {
    Instant start = Instant.now();
    RegisteredDevice defaultDevice = new RegisteredDevice();
    Instant end = Instant.now();

    assertEquals("", defaultDevice.getDeviceID());
    assertTrue(start.compareTo(defaultDevice.getLastRegistered()) <= 0);
    assertTrue(end.compareTo(defaultDevice.getLastRegistered()) >= 0);
  }

  @Test
  public void testEquals() {
    assertEquals(device1, device2);
    assertEquals(device1, device3);
    assertEquals(device2, device3);
    assertNotEquals(device1, device4);
    assertNotEquals(device3, device4);
  }

  @Test
  public void testHashCode() {
    assertEquals(device1.getDeviceID().hashCode(), device1.hashCode());
    assertEquals(device1.hashCode(), device2.hashCode());
    assertEquals(device1.hashCode(), device3.hashCode());
    assertEquals(device2.hashCode(), device3.hashCode());
    assertNotEquals(device1.hashCode(), device4.hashCode());
    assertNotEquals(device3.hashCode(), device4.hashCode());
  }
}
