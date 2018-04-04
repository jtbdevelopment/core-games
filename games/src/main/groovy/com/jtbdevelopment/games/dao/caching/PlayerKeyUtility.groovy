package com.jtbdevelopment.games.dao.caching

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.AbstractPlayer
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
    private static AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository

    @SuppressWarnings("GrMethodMayBeStatic")
    @Autowired
    void setPlayerRepository(final AbstractPlayerRepository playerRepository) {
        PlayerKeyUtility.playerRepository = playerRepository
    }

    static List<ID> collectPlayerIDs(final Iterable<Player<ID>> players) {
        if (players == null) {
            return Collections.<ID> emptyList()
        }
        return players.collect { Player<ID> p -> p.id }
    }

    static List<String> collectPlayerMD5s(final Iterable<Player<ID>> players) {
        if (players == null) {
            return Collections.<String> emptyList()
        }
        return players.collect { Player<ID> p -> p.md5 }
    }

    static List<String> collectPlayerSourceAndSourceIDs(final Iterable<Player<ID>> players) {
        if (players == null) {
            return Collections.<String> emptyList()
        }
        return players.collect { Player<ID> p -> (p.sourceAndSourceId) }
    }

    static List<String> collectSourceAndSourceIDs(final String source, final Iterable<String> sourceIds) {
        if (sourceIds == null || source == null) {
            return Collections.<String> emptyList()
        }
        return sourceIds.collect { String sourceId -> (AbstractPlayer.getSourceAndSourceId(source, sourceId)) }
    }

    static String md5FromID(final ID id) {
        def optional = playerRepository.findById(id)
        if (optional.present) {
            return optional.get().md5
        }
    }

    static String sourceAndSourceIDFromID(final ID id) {
        Optional<? extends Player> optional = playerRepository.findById(id)
        if (optional.present) {
            return optional.get().sourceAndSourceId
        }
        return null
    }
}
