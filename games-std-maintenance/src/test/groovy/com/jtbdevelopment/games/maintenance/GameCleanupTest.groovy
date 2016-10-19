package com.jtbdevelopment.games.maintenance

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 8/18/15
 * Time: 10:50 PM
 */
class GameCleanupTest extends GroovyTestCase {
    GameCleanup gameCleanup = new GameCleanup()

    void testDeleteOlderGames() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of('GMT')).minusDays(60)
        gameCleanup.gameRepository = [
                deleteByCreatedLessThan: {
                    ZonedDateTime cutoff ->
                        assert start.compareTo(cutoff) <= 0
                        assert start.plusMinutes(1).compareTo(cutoff) > 0
                        return 1L
                }
        ] as AbstractMultiPlayerGameRepository

        gameCleanup.deleteOlderGames()
    }
}
