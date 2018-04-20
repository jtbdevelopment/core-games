package com.jtbdevelopment.games.security.spring.userdetails

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.security.spring.LastLoginUpdater
import com.jtbdevelopment.games.security.spring.PlayerUserDetails
import org.mockito.Mockito

import static org.mockito.Mockito.when

/**
 * Date: 12/24/14
 * Time: 4:48 PM
 */
class PlayerUserDetailsServiceTest extends GameCoreTestCase {
    PlayerUserDetailsService userDetailsService = new PlayerUserDetailsService()

    void testLoadUserByUsername() {
        AbstractPlayerRepository repository = Mockito.mock(AbstractPlayerRepository.class)
        when(repository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, PTWO.sourceId)).thenReturn(PTWO)
        userDetailsService.playerRepository = repository
        LastLoginUpdater updater = Mockito.mock(LastLoginUpdater.class)
        Player updated = Mockito.mock(Player.class)
        when(updater.updatePlayerLastLogin(PTWO)).thenReturn(updated)
        userDetailsService.lastLoginUpdater = updater

        PlayerUserDetails d = userDetailsService.loadUserByUsername(PTWO.sourceId)
        assert d.effectiveUser == updated
        assert d.sessionUser == updated
    }

    void testNoLoadUserByUsername() {
        AbstractPlayerRepository repository = Mockito.mock(AbstractPlayerRepository.class)
        when(repository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, PTWO.sourceId)).thenReturn(null)
        userDetailsService.playerRepository = repository

        assert userDetailsService.loadUserByUsername(PTWO.sourceId) == null
    }
}
