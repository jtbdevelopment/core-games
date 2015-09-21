package com.jtbdevelopment.games.websocket

import groovy.transform.CompileStatic
import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.Universe
import org.springframework.stereotype.Component

/**
 * Date: 9/21/15
 * Time: 6:23 PM
 */
@Component
@CompileStatic
class AtmosphereBroadcasterFactory {
    BroadcasterFactory getBroadcasterFactory() {
        Universe.broadcasterFactory()
    }
}
