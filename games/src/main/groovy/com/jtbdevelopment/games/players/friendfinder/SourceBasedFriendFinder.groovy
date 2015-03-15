package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 12:03 PM
 */
@CompileStatic
interface SourceBasedFriendFinder {
    static final String FRIENDS_KEY = "friends";
    static final String MASKED_FRIENDS_KEY = "maskedFriends";
    static final String INVITABLE_FRIENDS_KEY = "invitableFriends";
    static final String NOT_FOUND_KEY = "notFoundFriends";

    boolean handlesSource(final String source);

    /*
        Return a set of data regarding friends
            At a minimum, the FRIENDS_KEY needs to be provided with a list of Players
     */

    Map<String, Set<Object>> findFriends(final Player player);
}
