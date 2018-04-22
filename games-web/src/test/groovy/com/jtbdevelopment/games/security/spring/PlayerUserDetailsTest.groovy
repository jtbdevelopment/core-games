package com.jtbdevelopment.games.security.spring

import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.PlayerRoles
import org.springframework.security.core.authority.SimpleGrantedAuthority

import static com.jtbdevelopment.games.GameCoreTestCase.*

/**
 * Date: 12/24/14
 * Time: 3:06 PM
 */
class PlayerUserDetailsTest extends GroovyTestCase {
    static ManualPlayer adminPlayer = makeSimpleManualPlayer("M1", "YAR!", true, false, true)
    static ManualPlayer manualPlayer = makeSimpleManualPlayer("M2", "YAR!", true, false, false)
    static ManualPlayer nonVerifiedPlayer = makeSimpleManualPlayer("M2", "YAR!", false, false, false)

    void testGetSessionUser() {
        PlayerUserDetails d = new PlayerUserDetails(PONE)
        assert d.sessionUser.is(PONE)
    }

    void testGetEffectiveUser() {
        PlayerUserDetails d = new PlayerUserDetails(PONE)
        assert d.sessionUser.is(PONE)
        assert d.effectiveUser.is(PONE)
    }

    void testSetEffectiveUser() {
        PlayerUserDetails d = new PlayerUserDetails(PONE)
        assert d.sessionUser.is(PONE)
        assert d.effectiveUser.is(PONE)
        d.setEffectiveUser(PTWO)
        assert d.sessionUser.is(PONE)
        assert d.effectiveUser.is(PTWO)
    }

    void testGetUserId() {
        PlayerUserDetails d = new PlayerUserDetails(PONE)
        d.setEffectiveUser(PTWO)
        assert PONE.idAsString == d.userId
    }

    void testGetAuthorities() {
        assert new PlayerUserDetails(manualPlayer).authorities == [new SimpleGrantedAuthority(PlayerRoles.PLAYER)]
        assert new PlayerUserDetails(adminPlayer).authorities == [
                new SimpleGrantedAuthority(PlayerRoles.PLAYER),
                new SimpleGrantedAuthority(PlayerRoles.ADMIN),
        ]
    }

    void testGetPassword() {
        assert new PlayerUserDetails(PONE).password == null
        assert new PlayerUserDetails(manualPlayer).password == manualPlayer.password
    }

    void testGetUsername() {

        assert new PlayerUserDetails(PONE).username == PONE.id
        assert new PlayerUserDetails(manualPlayer).username == manualPlayer.sourceId
    }

    void testIsAccountNonExpired() {
        assert new PlayerUserDetails(null).accountNonExpired
    }

    void testIsAccountNonLocked() {
        assert new PlayerUserDetails(PONE).accountNonLocked
        assert new PlayerUserDetails(manualPlayer).accountNonLocked
        assert !(new PlayerUserDetails(nonVerifiedPlayer).accountNonLocked)
    }

    void testIsCredentialsNonExpired() {
        assert new PlayerUserDetails(null).credentialsNonExpired
    }

    void testIsEnabled() {
        assert new PlayerUserDetails(PONE).enabled
        assert !(new PlayerUserDetails(PINACTIVE1).enabled)
    }
}
