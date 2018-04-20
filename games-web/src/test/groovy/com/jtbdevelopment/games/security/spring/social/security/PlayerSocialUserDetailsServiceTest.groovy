package com.jtbdevelopment.games.security.spring.social.security

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.security.spring.LastLoginUpdater
import com.jtbdevelopment.games.security.spring.PlayerUserDetails
import org.springframework.social.security.SocialUserDetails

/**
 * Date: 1/7/15
 * Time: 6:51 PM
 */
class PlayerSocialUserDetailsServiceTest extends GameCoreTestCase {
    PlayerSocialUserDetailsService service = new PlayerSocialUserDetailsService()

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        service.stringToIDConverter = new StringToIDConverter<String>() {
            @Override
            String convert(final String source) {
                return source?.reverse()
            }
        }
    }

    void testReturnsWrappedPlayerIfFoundAfterCallingLoginUpdated() {
        Player player = makeSimplePlayer("4524")
        Player playerCopy = makeSimplePlayer("4524")
        service.playerRepository = [
                findById: {
                    String it ->
                        assert it == player.idAsString.reverse()
                        return Optional.of(player)
                }
        ] as AbstractPlayerRepository
        service.lastLoginUpdater = [
                updatePlayerLastLogin: {
                    Player p ->
                        assert p.is(player)
                        playerCopy
                }
        ] as LastLoginUpdater

        SocialUserDetails userDetails = service.loadUserByUserId(player.id)
        assert userDetails instanceof PlayerUserDetails
        assert userDetails.sessionUser.is(playerCopy)
    }

    void testReturnsNullIfNotFound() {
        String id = 'ANID'
        service.playerRepository = [
                findById: {
                    String it ->
                        assert it == id.reverse()
                        return Optional.empty()
                }
        ] as AbstractPlayerRepository

        assertNull service.loadUserByUserId(id)
    }
}
