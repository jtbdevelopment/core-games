package com.jtbdevelopment.games.push.websocket

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.push.PushProperties
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.websocket.WebSocketPublicationListener
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private static final Logger logger = LoggerFactory.getLogger(PushWebSocketPublicationListener.class)
    static final String WEB_SOCKET_TRACKING_MAP = "PUSH_TRACKING_MAP"

    protected static final ZoneId GMT = ZoneId.of("GMT")
    protected static final int CUTOFF_DAYS = 30
    protected static ZonedDateTime registeredCutoff

    @Autowired
    HazelcastInstance hazelcastInstance

    @Autowired
    PushNotifierFilter pushNotifierFilter

    @Autowired
    PushProperties pushProperties

    protected ConcurrentMap<GamePublicationTracker, Boolean> trackingMap

    @PostConstruct
    void setup() {
        if (pushProperties.enabled) {
            trackingMap = hazelcastInstance.getMap(WEB_SOCKET_TRACKING_MAP)
            ((IMap) trackingMap).addEntryListener(pushNotifierFilter, true)

            computeRegistrationCutoff()
            Thread.start {
                Thread.sleep(60 * 60 * 1000)  // hourly
                computeRegistrationCutoff()
            }
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
    void publishedGameUpdateToPlayer(final Player player, final Game game, final boolean published) {
        if (pushProperties.enabled && player.registeredDevices.find {
            it.lastRegistered > registeredCutoff
        }) {
            GamePublicationTracker tracker = new GamePublicationTracker(
                    pid: player.id,
                    gid: (Serializable) game.id)
            boolean was, is
            if (published) {
                logger.trace("Forcing published status on " + tracker)
                was = trackingMap.put(tracker, published)
            } else {
                logger.trace("putIfAbsent publish status on " + tracker)
                was = trackingMap.putIfAbsent(tracker, published)
            }
            if (logger.isTraceEnabled()) {
                is = trackingMap.get(tracker)
                logger.trace("Was " + was + ", is " + is)
            }
        }
    }
}
