package com.jtbdevelopment.games.security.spring

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 8/16/2015
 * Time: 8:12 PM
 */
class LastLoginUpdaterTest extends GameCoreTestCase {
    LastLoginUpdater updater = new LastLoginUpdater()
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"))

    void testUpdatesPlayerIfLastLoginIsNull() {
        def player = new GameCoreTestCase.StringPlayer(lastLogin: null)
        def playerCopy = new GameCoreTestCase.StringPlayer(lastLogin: null)
        updater.playerRepository = [
                save: {
                    p ->
                        assert p.is(player)
                        assertNotNull player.lastLogin
                        assert now.compareTo(player.lastLogin) <= 0
                        playerCopy
                }
        ] as AbstractPlayerRepository
        assert playerCopy.is(updater.updatePlayerLastLogin(player));

        assertNotNull player.lastLogin
        assert now.compareTo(player.lastLogin) <= 0
    }

    void testUpdatesPlayerIfLastLoginIsOlderThan15Minutes() {
        def player = new GameCoreTestCase.StringPlayer(lastLogin: now.minusMinutes(16))
        def playerCopy = new GameCoreTestCase.StringPlayer(lastLogin: null)
        updater.playerRepository = [
                save: {
                    p ->
                        assert p.is(player)
                        assertNotNull player.lastLogin
                        assert now.compareTo(player.lastLogin) <= 0
                        assert now.minusMinutes(1).compareTo(player.lastLogin) < 0
                        playerCopy
                }
        ] as AbstractPlayerRepository
        assert playerCopy.is(updater.updatePlayerLastLogin(player));

        assertNotNull player.lastLogin
        assert now.compareTo(player.lastLogin) <= 0
        assert now.minusMinutes(1).compareTo(player.lastLogin) <= 0
    }

    void testDoesNotUpdatePlayerIfLastLoginIs15Minutes() {
        def fifteenMinutes = now.minusMinutes(15)
        def player = new GameCoreTestCase.StringPlayer(lastLogin: fifteenMinutes)
        updater.playerRepository = [
                save: {
                    p ->
                        fail 'unexpected update'
                }
        ] as AbstractPlayerRepository
        assert player.is(updater.updatePlayerLastLogin(player));

        assert fifteenMinutes.compareTo(player.lastLogin) == 0
    }
}
