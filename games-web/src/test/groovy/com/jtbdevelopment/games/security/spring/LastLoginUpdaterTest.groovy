package com.jtbdevelopment.games.security.spring

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.stringimpl.StringPlayer

import java.time.Instant

/**
 * Date: 8/16/2015
 * Time: 8:12 PM
 */
class LastLoginUpdaterTest extends GameCoreTestCase {
    LastLoginUpdater updater = new LastLoginUpdater()
    Instant now = Instant.now()

    void testUpdatesPlayerIfLastLoginIsNull() {
        def player = new StringPlayer(lastLogin: null)
        def playerCopy = new StringPlayer(lastLogin: null)
        updater.playerRepository = [
                save: {
                    p ->
                        assert p.is(player)
                        assertNotNull player.lastLogin
                        assert now <= player.lastLogin
                        playerCopy
                }
        ] as AbstractPlayerRepository
        assert playerCopy.is(updater.updatePlayerLastLogin(player));

        assertNotNull player.lastLogin
        assert now <= player.lastLogin
    }

    void testUpdatesPlayerIfLastLoginIsOlderThan15Minutes() {
        def player = new StringPlayer(lastLogin: now.minusSeconds(16 * 60))
        def playerCopy = new StringPlayer(lastLogin: null)
        updater.playerRepository = [
                save: {
                    p ->
                        assert p.is(player)
                        assertNotNull player.lastLogin
                        assert now <= player.lastLogin
                        assert now.minusSeconds(60) < player.lastLogin
                        playerCopy
                }
        ] as AbstractPlayerRepository
        assert playerCopy.is(updater.updatePlayerLastLogin(player))

        assertNotNull player.lastLogin
        assert now <= player.lastLogin
        assert now.minusSeconds(60) <= player.lastLogin
    }

    void testDoesNotUpdatePlayerIfLastLoginIs15Minutes() {
        def fifteenMinutes = now.minusSeconds(15 * 60)
        def player = new StringPlayer(lastLogin: fifteenMinutes)
        updater.playerRepository = [
                save: {
                    p ->
                        fail 'unexpected update'
                }
        ] as AbstractPlayerRepository
        assert player.is(updater.updatePlayerLastLogin(player));

        assert fifteenMinutes == player.lastLogin
    }
}
