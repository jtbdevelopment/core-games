package com.jtbdevelopment.games.security.spring

import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerRoles
import com.jtbdevelopment.games.security.SessionUserInfo
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.social.security.SocialUserDetails

/**
 * Date: 12/14/14
 * Time: 5:27 PM
 */
@CompileStatic
class PlayerUserDetails<ID extends Serializable> implements SocialUserDetails, UserDetails, SessionUserInfo {

    final private Player<ID> player
    private Player<ID> effectivePlayer

    final List<SimpleGrantedAuthority> grantedAuthorities = [new SimpleGrantedAuthority(PlayerRoles.PLAYER)]

    PlayerUserDetails(final Player<ID> player) {
        this.player = player
        this.effectivePlayer = player
        if (player && player.adminUser) {
            grantedAuthorities.push(new SimpleGrantedAuthority(PlayerRoles.ADMIN))
        }
    }

    @Override
    Player<ID> getSessionUser() {
        return player
    }

    @Override
    Player<ID> getEffectiveUser() {
        return effectivePlayer
    }

    @Override
    void setEffectiveUser(final Player player) {
        this.effectivePlayer = player
    }

    @Override
    String getUserId() {
        return player.idAsString
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities
    }

    @Override
    String getPassword() {
        if (player instanceof ManualPlayer) {
            return player.password
        }
        return null
    }

    @Override
    String getUsername() {
        if (player instanceof ManualPlayer) {
            return player.getSourceId()
        }
        return player.idAsString
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        if (player instanceof ManualPlayer) {
            return player.verified
        }
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return !player.disabled
    }
}
