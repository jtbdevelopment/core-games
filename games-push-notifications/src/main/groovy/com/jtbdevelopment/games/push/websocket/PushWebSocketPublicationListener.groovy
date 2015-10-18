package com.jtbdevelopment.games.push.websocket

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.websocket.WebSocketPublicationListener
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentMap

/**
 * Date: 10/10/2015
 * Time: 3:35 PM
 */
@CompileStatic
@Component
class PushWebSocketPublicationListener implements WebSocketPublicationListener {
    static final String WEBSOCKET_TRACKING_MAP = "PUSH_TRACKING_MAP"

    protected static final ZoneId GMT = ZoneId.of("GMT")
    protected static final int CUTOFF_DAYS = 30
    protected static ZonedDateTime registeredCutoff

    @Autowired
    HazelcastInstance hazelcastInstance

    @Autowired
    PushNotifierFilter pushNotifierFilter

    protected ConcurrentMap<GamePublicationTracker, Boolean> trackingMap

    @PostConstruct
    void setup() {
        trackingMap = hazelcastInstance.getMap(WEBSOCKET_TRACKING_MAP)
        ((IMap) trackingMap).addEntryListener(pushNotifierFilter, true)

        computeRegistrationCutoff()
        Thread.start {
            Thread.sleep(60 * 60 * 1000)  // hourly
            computeRegistrationCutoff()
        }
    }

    protected static void computeRegistrationCutoff() {
        registeredCutoff = ZonedDateTime.now(GMT).minusDays(CUTOFF_DAYS)
    }

    @Override
    void publishedPlayerUpdate(final Player player, final boolean status) {
        //  Ignore - if they are logged out, they will refresh on login
    }

    @Override
    void publishedGameUpdateToPlayer(final Player player, final MultiPlayerGame game, final boolean status) {
        if (player.registeredDevices.find { it.lastRegistered.compareTo(registeredCutoff) > 0 }) {
            GamePublicationTracker tracker = new GamePublicationTracker(
                    pid: (Serializable) player.id,
                    gid: (Serializable) game.id)
            if (status) {
                trackingMap.put(tracker, status)
            } else {
                trackingMap.putIfAbsent(tracker, status)
            }
        }
    }
}
