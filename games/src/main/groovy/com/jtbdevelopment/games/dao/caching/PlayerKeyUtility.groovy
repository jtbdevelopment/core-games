package com.jtbdevelopment.games.dao.caching

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 2/27/15
 * Time: 9:46 PM
 */
@CompileStatic
@Component
class PlayerKeyUtility<ID extends Serializable> {
    private static AbstractPlayerRepository playerRepository

    @Autowired
    void setPlayerRepository(final AbstractPlayerRepository playerRepository) {
        PlayerKeyUtility.playerRepository = playerRepository
    }

    static List<ID> collectPlayerIDs(final Iterable<Player<ID>> players) {
        if (!players) {
            return Collections.<ID> emptyList()
        }
        return players.collect { Player<ID> p -> p.id }
    }

    static List<String> collectPlayerMD5s(final Iterable<Player<ID>> players) {
        if (!players) {
            return Collections.<String> emptyList()
        }
        return players.collect { Player<ID> p -> p.md5 }
    }

    static List<String> collectPlayerSourceAndSourceIDs(final Iterable<Player<ID>> players) {
        if (!players) {
            return Collections.<String> emptyList()
        }
        return players.collect { Player<ID> p -> (p.source + "/" + p.sourceId) }
    }

    static List<String> collectSourceAndSourceIDs(final String source, final Iterable<String> sourceIds) {
        if (!sourceIds || !source) {
            return Collections.<String> emptyList()
        }
        return sourceIds.collect { String sourceId -> (source + "/" + sourceId) }
    }

    static String md5FromID(final ID id) {
        return playerRepository.findOne(id)?.md5
    }

    static String sourceAndSourceIDFromID(final ID id) {
        Player<ID> player = playerRepository.findOne(id)
        if (player) {
            return player.source + "/" + player.sourceId
        }
        return null
    }
}
