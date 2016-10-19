package com.jtbdevelopment.games.maintenance

import com.jtbdevelopment.games.dao.AbstractPlayerRepository

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 8/18/15
 * Time: 10:50 PM
 */
class PlayerCleanupTest extends GroovyTestCase {
    PlayerCleanup playerCleanup = new PlayerCleanup()

    void testDeleteOlderGames() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of('GMT')).minusDays(90)
        playerCleanup.playerRepository = [
                deleteByLastLoginLessThan: {
                    ZonedDateTime cutoff ->
                        assert start.compareTo(cutoff) <= 0
                        assert start.plusMinutes(1).compareTo(cutoff) > 0
                        return 1L
                }
        ] as AbstractPlayerRepository

        playerCleanup.deleteInactivePlayers()
    }
}
