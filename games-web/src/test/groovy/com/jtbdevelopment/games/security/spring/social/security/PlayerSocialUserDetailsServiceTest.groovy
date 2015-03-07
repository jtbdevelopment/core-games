package com.jtbdevelopment.games.security.spring.social.security

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.security.spring.PlayerUserDetails
import org.springframework.social.security.SocialUserDetails

/**
 * Date: 1/7/15
 * Time: 6:51 PM
 */
class PlayerSocialUserDetailsServiceTest extends GroovyTestCase {
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

    void testReturnsWrappedPlayerIfFound() {
        String id = 'ANID'
        Player player = [] as Player
        service.playerRepository = [
                findOne: {
                    String it ->
                        assert it == id.reverse()
                        return player
                }
        ] as AbstractPlayerRepository

        SocialUserDetails userDetails = service.loadUserByUserId(id)
        assert userDetails instanceof PlayerUserDetails
        assert userDetails.sessionUser.is(player)
    }

    void testReturnsNullIfNotFound() {
        String id = 'ANID'
        Player player = [] as Player
        service.playerRepository = [
                findOne: {
                    String it ->
                        assert it == id.reverse()
                        return null
                }
        ] as AbstractPlayerRepository

        assertNull service.loadUserByUserId(id)
    }
}
