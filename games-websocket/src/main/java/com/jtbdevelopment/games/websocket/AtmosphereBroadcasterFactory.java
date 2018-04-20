package com.jtbdevelopment.games.websocket;

import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.Universe;
import org.springframework.stereotype.Component;

/**
 * Date: 9/21/15 Time: 6:23 PM
 */
@Component
public class AtmosphereBroadcasterFactory {

    public BroadcasterFactory getBroadcasterFactory() {
        return Universe.broadcasterFactory();
    }

}
