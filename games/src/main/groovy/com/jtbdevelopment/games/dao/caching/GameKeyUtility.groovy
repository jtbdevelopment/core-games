package com.jtbdevelopment.games.dao.caching

import com.jtbdevelopment.games.games.Game
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 2/27/15
 * Time: 9:46 PM
 */
@CompileStatic
@Component
class GameKeyUtility<ID extends Serializable> {
    static List collectGameIDs(final Iterable<Game> games) {
        if (!games) {
            return Collections.<ID> emptyList()
        }
        return games.collect { Game g -> g.id }
    }
}
