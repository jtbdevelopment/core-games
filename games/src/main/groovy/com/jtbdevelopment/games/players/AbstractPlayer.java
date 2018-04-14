package com.jtbdevelopment.games.players;

import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.util.StringUtils;

/**
 * Date: 11/3/14 Time: 6:53 AM
 */
public abstract class AbstractPlayer<ID extends Serializable>
    implements Cloneable, Player<ID>, Serializable {

  private String source;
  private String sourceId;
  private String displayName;
  private String imageUrl;
  private String profileUrl;
  private Set<RegisteredDevice> registeredDevices = new HashSet<>();
  @CreatedDate
  private Instant created = Instant.now();
  private Instant lastLogin = Instant.now().minusSeconds(60 * 60 * 24 * 365);
  private String lastVersionNotes = "";
  private boolean disabled = false;
  private boolean adminUser = false;
  private PlayerPayLevel payLevel = PlayerPayLevel.FreeToPlay;
  private GameSpecificPlayerAttributes gameSpecificPlayerAttributes;

  public static String getSourceAndSourceId(final String source, final String sourceId) {
    return (source != null && sourceId != null) ? (source + "/" + sourceId) : null;
  }

  public boolean equals(final Object o) {
    if (!(o instanceof AbstractPlayer)) {
      return false;
    }

    final AbstractPlayer player = (AbstractPlayer) o;

    return getId().equals(player.getId());
  }

  public int hashCode() {
    String stringId = this.getIdAsString();
    return stringId != null ? stringId.hashCode() : 0;
  }

  protected abstract String getMd5Internal();

  public String getMd5() {
    if (StringUtils.isEmpty(getMd5Internal())) {
      computeMD5Hex();
    }

    return this.getMd5Internal();
  }

  protected abstract void setMd5(final String md5);

  @Override
  public String toString() {
    return "Player{" + "id='" + getId() + "\'" + ", source='" + source + "\'" + ", sourceId='"
        + sourceId + "\'" + ", displayName='" + displayName + "\'" + ", disabled=" + disabled + "}";
  }

  protected void computeMD5Hex() {
    String md5;
    if (getIdAsString() == null || source == null || displayName == null || sourceId == null) {
      md5 = "";
    } else {
      String key = getIdAsString() + source + displayName + sourceId;
      md5 = DigestUtils.md5Hex(key);
    }

    setMd5(md5);
  }

  @Override
  public String getSourceAndSourceId() {
    return getSourceAndSourceId(source, sourceId);
  }

  @Override
  public void updateRegisteredDevice(final RegisteredDevice device) {
    if (registeredDevices.contains(device)) {
      registeredDevices.remove(device);
    }

    registeredDevices.add(device);
  }

  @Override
  public void removeRegisteredDevice(final RegisteredDevice device) {
    registeredDevices.remove(device);
  }

  public String getSource() {
    return source;
  }

  public void setSource(final String source) {
    if (StringUtils.isEmpty(this.source)) {
      this.source = source;
      computeMD5Hex();
    }

  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(final String sourceId) {
    this.sourceId = sourceId;
    computeMD5Hex();
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
    computeMD5Hex();
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  public void setProfileUrl(String profileUrl) {
    this.profileUrl = profileUrl;
  }

  public Set<RegisteredDevice> getRegisteredDevices() {
    return registeredDevices;
  }

  public void setRegisteredDevices(Set<RegisteredDevice> registeredDevices) {
    this.registeredDevices = registeredDevices;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Instant lastLogin) {
    this.lastLogin = lastLogin;
  }

  public String getLastVersionNotes() {
    return lastVersionNotes;
  }

  public void setLastVersionNotes(String lastVersionNotes) {
    this.lastVersionNotes = lastVersionNotes;
  }

  public boolean getDisabled() {
    return disabled;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public boolean getAdminUser() {
    return adminUser;
  }

  public boolean isAdminUser() {
    return adminUser;
  }

  public void setAdminUser(boolean adminUser) {
    this.adminUser = adminUser;
  }

  public PlayerPayLevel getPayLevel() {
    return payLevel;
  }

  public void setPayLevel(PlayerPayLevel payLevel) {
    this.payLevel = payLevel;
  }

  public <T extends GameSpecificPlayerAttributes> T getGameSpecificPlayerAttributes() {
    return (T) gameSpecificPlayerAttributes;
  }

  public void setGameSpecificPlayerAttributes(
      final GameSpecificPlayerAttributes gameSpecificPlayerAttributes) {
    this.gameSpecificPlayerAttributes = gameSpecificPlayerAttributes;
    if (gameSpecificPlayerAttributes != null) {
      gameSpecificPlayerAttributes.setPlayer(this);
    }

  }
}
