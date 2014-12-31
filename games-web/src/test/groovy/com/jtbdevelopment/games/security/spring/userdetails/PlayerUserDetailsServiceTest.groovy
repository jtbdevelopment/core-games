package com.jtbdevelopment.games.security.spring.userdetails

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.security.spring.PlayerUserDetails

/**
 * Date: 12/24/14
 * Time: 4:48 PM
 */
class PlayerUserDetailsServiceTest extends GameCoreTestCase {
    PlayerUserDetailsService userDetailsService = new PlayerUserDetailsService()

    void testLoadUserByUsername() {
        def repo = [
                findBySourceAndSourceId: {
                    String source, String username ->
                        assert source == ManualPlayer.MANUAL_SOURCE
                        assert username == PTWO.sourceId
                        PTWO
                }
        ] as AbstractPlayerRepository<String>
        userDetailsService.playerRepository = repo

        PlayerUserDetails d = userDetailsService.loadUserByUsername(PTWO.sourceId)
        assert d.effectiveUser == PTWO
        assert d.sessionUser == PTWO
    }

    void testNoLoadUserByUsername() {
        def repo = [
                findBySourceAndSourceId: {
                    String source, String username ->
                        assert source == ManualPlayer.MANUAL_SOURCE
                        assert username == PTWO.sourceId
                        null
                }
        ] as AbstractPlayerRepository
        userDetailsService.playerRepository = repo

        assert userDetailsService.loadUserByUsername(PTWO.sourceId) == null
    }
}
