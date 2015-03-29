package com.jtbdevelopment.games.events

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 12/8/14
 * Time: 6:40 PM
 */
@CompileStatic
interface GamePublisher<IMPL extends Game> {
    //  Returns game primarily to allow easy chaining
    IMPL publish(final IMPL game, final Player initiatingPlayer)

    IMPL publish(final IMPL game, final Player initiatingPlayer, boolean initiatingServer)
}
