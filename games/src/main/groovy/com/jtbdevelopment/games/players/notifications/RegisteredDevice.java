package com.jtbdevelopment.games.players.notifications;

import java.io.Serializable;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 10/16/15 Time: 6:52 AM
 */
@Component
public class RegisteredDevice implements Serializable {

  private String deviceID = "";
  private Instant lastRegistered = Instant.now();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RegisteredDevice)) {
      return false;
    }

    RegisteredDevice that = (RegisteredDevice) o;

    return deviceID.equals(that.deviceID);
  }

  public int hashCode() {
    return deviceID.hashCode();
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }

  public Instant getLastRegistered() {
    return lastRegistered;
  }

  public void setLastRegistered(Instant lastRegistered) {
    this.lastRegistered = lastRegistered;
  }
}
