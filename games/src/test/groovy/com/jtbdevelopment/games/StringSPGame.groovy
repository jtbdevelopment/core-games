package com.jtbdevelopment.games

import com.jtbdevelopment.games.state.AbstractSinglePlayerGame

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */

class StringSPGame extends AbstractSinglePlayerGame<String, Object> {
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
