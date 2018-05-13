package com.jtbdevelopment.games.players;

import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import org.springframework.data.annotation.Transient;

/**
 * Date: 12/30/2014 Time: 11:08 AM
 */
public interface Player<ID extends Serializable> {

  <T extends GameSpecificPlayerAttributes> T getGameSpecificPlayerAttributes();

  <T extends GameSpecificPlayerAttributes> void setGameSpecificPlayerAttributes(
      final T gameSpecificPlayerAttributes);

  ID getId();

  void setId(final ID id);

  @Transient
  String getIdAsString();

  String getSource();

  void setSource(final String source);

  String getSourceId();

  void setSourceId(final String sourceId);

  String getDisplayName();

  void setDisplayName(final String displayName);

  String getImageUrl();

  void setImageUrl(String imageUrl);

  String getProfileUrl();

  void setProfileUrl(String profileUrl);

  boolean isDisabled();

  void setDisabled(boolean disabled);

  PlayerPayLevel getPayLevel();

  void setPayLevel(final PlayerPayLevel payLevel);

  boolean isAdminUser();

  void setAdminUser(boolean adminUser);

  String getMd5();

  String getSourceAndSourceId();

  Instant getCreated();

  Instant getLastLogin();

  void setLastLogin(final Instant lastLogin);

  String getLastVersionNotes();

  void setLastVersionNotes(final String lastVersionNotes);

  Set<RegisteredDevice> getRegisteredDevices();

  void setRegisteredDevices(final Set<RegisteredDevice> devices);

  void updateRegisteredDevice(final RegisteredDevice device);

  void removeRegisteredDevice(final RegisteredDevice device);
}
