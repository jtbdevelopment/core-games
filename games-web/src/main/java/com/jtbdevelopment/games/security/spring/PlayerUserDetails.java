package com.jtbdevelopment.games.security.spring;

import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.security.SocialUserDetails;

/**
 * Date: 12/14/14 Time: 5:27 PM
 */
public class PlayerUserDetails<ID extends Serializable> implements SocialUserDetails, UserDetails,
    SessionUserInfo {

  private final Player<ID> player;
  private final List<SimpleGrantedAuthority> grantedAuthorities = new LinkedList<>(
      Collections.singletonList(new SimpleGrantedAuthority(PlayerRoles.PLAYER))
  );
  private Player<ID> effectivePlayer;

  public PlayerUserDetails(final Player<ID> player) {
    this.player = player;
    this.effectivePlayer = player;
    if (player != null && player.getAdminUser()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(PlayerRoles.ADMIN));
    }

  }

  @Override
  public Player<ID> getSessionUser() {
    return player;
  }

  @Override
  public Player<ID> getEffectiveUser() {
    return effectivePlayer;
  }

  @Override
  public void setEffectiveUser(final Player player) {
    this.effectivePlayer = player;
  }

  @Override
  public String getUserId() {
    return player.getIdAsString();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return grantedAuthorities;
  }

  @Override
  public String getPassword() {
    if (player instanceof ManualPlayer) {
      return ((ManualPlayer) player).getPassword();
    }

    return null;
  }

  @Override
  public String getUsername() {
    if (player instanceof ManualPlayer) {
      return player.getSourceId();
    }

    return player.getIdAsString();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    if (player instanceof ManualPlayer) {
      return ((ManualPlayer) player).getVerified();
    }

    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return !player.getDisabled();
  }

}
