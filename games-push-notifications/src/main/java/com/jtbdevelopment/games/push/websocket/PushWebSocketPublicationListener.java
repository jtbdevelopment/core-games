package com.jtbdevelopment.games.push.websocket;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.push.PushProperties;
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker;
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.websocket.WebSocketPublicationListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 10/10/2015 Time: 3:35 PM
 */
@Component
public class PushWebSocketPublicationListener implements WebSocketPublicationListener {

  private static final Logger logger = LoggerFactory
      .getLogger(PushWebSocketPublicationListener.class);
  public static final String WEB_SOCKET_TRACKING_MAP = "PUSH_TRACKING_MAP";
  private static final ZoneId GMT = ZoneId.of("GMT");
  private static final int CUTOFF_DAYS = 30;
  private static Instant registeredCutoff;
  @Autowired
  protected HazelcastInstance hazelcastInstance;
  @Autowired
  protected PushNotifierFilter pushNotifierFilter;
  @Autowired
  protected PushProperties pushProperties;
  protected ConcurrentMap<GamePublicationTracker, Boolean> trackingMap;

  protected static void computeRegistrationCutoff() {
    registeredCutoff = ZonedDateTime.now(GMT).minusDays(CUTOFF_DAYS).toInstant();
  }

  public static String getWEB_SOCKET_TRACKING_MAP() {
    return WEB_SOCKET_TRACKING_MAP;
  }

  @PostConstruct
  public void setup() {
    if (pushProperties.isEnabled()) {
      trackingMap = hazelcastInstance.getMap(WEB_SOCKET_TRACKING_MAP);
      ((IMap) trackingMap).addEntryListener(pushNotifierFilter, true);

      computeRegistrationCutoff();
      Thread thread = new Thread(() -> {
        try {
          Thread.sleep(60 * 60 * 1000);// hourly
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        computeRegistrationCutoff();
      });
      thread.start();

    }

  }

  @Override
  public void publishedPlayerUpdate(final Player<?> player, final boolean status) {
    //  Ignore - if they are logged out, they will refresh on login
  }

  @Override
  public void publishedGameUpdateToPlayer(
      final Player<?> player,
      final Game game,
      final boolean published) {
    if (pushProperties.isEnabled() &&
        player.getRegisteredDevices()
            .stream()
            .anyMatch(d -> d.getLastRegistered().compareTo(registeredCutoff) > 0)) {
      GamePublicationTracker tracker = new GamePublicationTracker();
      tracker.setPid(player.getId());
      tracker.setGid(game.getId());
      Boolean was;
      Boolean is;
      if (published) {
        logger.trace("Forcing published status on " + tracker);
        was = trackingMap.put(tracker, published);
      } else {
        logger.trace("putIfAbsent publish status on " + tracker);
        was = trackingMap.putIfAbsent(tracker, published);
      }

      if (logger.isTraceEnabled()) {
        is = trackingMap.get(tracker);
        logger.trace("Was " + was + ", is " + is);
      }

    }

  }
}
