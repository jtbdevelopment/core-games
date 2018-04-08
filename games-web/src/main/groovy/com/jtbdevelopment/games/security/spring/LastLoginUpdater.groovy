package com.jtbdevelopment.games.security.spring

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/**
 * Date: 8/16/2015
 * Time: 8:02 PM
 */
@CompileStatic
@Component
class LastLoginUpdater {
    private static final ZoneId GMT = ZoneId.of("GMT");
    private static final int THRESHOLD = 15 // minutes

    @Autowired
    AbstractPlayerRepository playerRepository

    Player updatePlayerLastLogin(final Player player) {
        Instant now = Instant.now()
        if (player.lastLogin == null || Duration.between(player.lastLogin, now).toMinutes() > THRESHOLD) {
            player.lastLogin = now
            return playerRepository.save(player)
        }
        return player
    }
}
