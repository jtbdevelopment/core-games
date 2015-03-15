package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 11/26/14
 * Time: 8:51 PM
 */
@Component
@CompileStatic
class PlayerMasker {
    @SuppressWarnings("GrMethodMayBeStatic")
    Map<String, String> maskFriends(final Set<? extends Player> friends) {
        friends.collectEntries {
            Player p ->
                [p.md5, p.displayName]
        }
    }
}
