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

    public static final String MASKED_MD5 = 'md5'
    public static final String DISPLAY_NAME = 'displayName'

    @SuppressWarnings("GrMethodMayBeStatic")
    @Deprecated
    Map<String, String> maskFriends(final Set<? extends Player> friends) {
        friends.collectEntries {
            Player p ->
                [p.md5, p.displayName]
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    List<Map<String, String>> maskFriendsV2(final Set<? extends Player> friends) {
        friends.collect {
            Player p ->
                new HashMap<>([(MASKED_MD5): p.md5, (DISPLAY_NAME): p.displayName])
        }
    }
}
