package com.jtbdevelopment.games.security

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 12:06 PM
 *
 * A generic holder that can be served up however possible
 */
@CompileStatic
interface SessionUserInfo<ID extends Serializable> {
    Player<ID> getSessionUser();

    Player<ID> getEffectiveUser();

    void setEffectiveUser(final Player<ID> player);
}