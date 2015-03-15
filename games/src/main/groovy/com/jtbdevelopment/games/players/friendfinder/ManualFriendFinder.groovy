package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 11/26/14
 * Time: 1:09 PM
 */
@CompileStatic
@Component
class ManualFriendFinder implements SourceBasedFriendFinder {
    @Autowired
    AbstractPlayerRepository playerRepository

    @Override
    boolean handlesSource(final String source) {
        return ManualPlayer.MANUAL_SOURCE == source
    }

    @Override
    Map<String, Set<Object>> findFriends(final Player player) {
        Set<? extends Player> players = playerRepository.findBySourceAndDisabled(ManualPlayer.MANUAL_SOURCE, false) as Set
        players.remove(player)
        return [(FRIENDS_KEY): players]
    }
}
