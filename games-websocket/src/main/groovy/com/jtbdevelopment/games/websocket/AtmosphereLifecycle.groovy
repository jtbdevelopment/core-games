package com.jtbdevelopment.games.websocket

import groovy.transform.CompileStatic
import org.atmosphere.cpr.Universe
import org.springframework.context.Lifecycle
import org.springframework.stereotype.Component

/**
 * Date: 6/4/15
 * Time: 12:15 AM
 */
@CompileStatic
@Component
class AtmosphereLifecycle implements Lifecycle {
    @Override
    void start() {

    }

    @Override
    void stop() {
        Universe.framework()?.destroy()
    }

    @Override
    boolean isRunning() {
        return false
    }
}
