package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 11/26/14
 * Time: 1:09 PM
 */
@CompileStatic
class AbstractManualFriendFinder<ID extends Serializable> implements SourceBasedFriendFinder<ID> {
    public static final String MANUAL = "MANUAL"
    @Autowired
    AbstractPlayerRepository<ID> playerRepository

    @Override
    boolean handlesSource(final String source) {
        return MANUAL == source
    }

    @Override
    Map<String, Set<Object>> findFriends(final Player<ID> player) {
        Set<? extends Player<ID>> players = playerRepository.findBySourceAndDisabled(MANUAL, false) as Set
        players.remove(player)
        return [(FRIENDS_KEY): players]
    }
}
