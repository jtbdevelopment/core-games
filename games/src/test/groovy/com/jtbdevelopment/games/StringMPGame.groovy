package com.jtbdevelopment.games

import com.jtbdevelopment.games.state.AbstractMultiPlayerGame

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */
class StringMPGame extends AbstractMultiPlayerGame<String, Object> implements Cloneable {
    String id

    @Override
    String getIdAsString() {
        return id
    }

    String previousId

    @Override
    String getPreviousIdAsString() {
        return previousId
    }
}

