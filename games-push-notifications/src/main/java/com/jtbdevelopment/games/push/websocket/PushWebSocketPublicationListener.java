package com.jtbdevelopment.games.push.websocket;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.push.PushProperties;
import com.jtbdevelopment.games.push.notifications.GamePublicationTracker;
import com.jtbdevelopment.games.push.notifications.PushNotifierFilter;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.websocket.WebSocketPublicationListener;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 10/10/2015 Time: 3:35 PM
 */
@Component
public class PushWebSocketPublicationListener<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>>
    implements WebSocketPublicationListener<ID, Instant, FEATURES, IMPL, P> {

  private static final Logger logger = LoggerFactory
      .getLogger(PushWebSocketPublicationListener.class);
  public static final String WEB_SOCKET_TRACKING_MAP = "PUSH_TRACKING_MAP";
  private static final ZoneId GMT = ZoneId.of("GMT");
  private static final int CUTOFF_DAYS = 30;
  private static Instant registeredCutoff;
  private final PushNotifierFilter<ID, FEATURES, IMPL, P> pushNotifierFilter;
  private final PushProperties pushProperties;
  private final ConcurrentMap<GamePublicationTracker, Boolean> trackingMap;

  PushWebSocketPublicationListener(
      final HazelcastInstance hazelcastInstance,
      final PushNotifierFilter<ID, FEATURES, IMPL, P> pushNotifierFilter,
      final PushProperties pushProperties) {
    this.pushNotifierFilter = pushNotifierFilter;
    this.pushProperties = pushProperties;
    trackingMap = hazelcastInstance.getMap(WEB_SOCKET_TRACKING_MAP);
    setup();
  }

  static void computeRegistrationCutoff() {
    registeredCutoff = ZonedDateTime.now(GMT).minusDays(CUTOFF_DAYS).toInstant();
  }

  public static String getWEB_SOCKET_TRACKING_MAP() {
    return WEB_SOCKET_TRACKING_MAP;
  }

  private void setup() {
    if (pushProperties.isEnabled()) {
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
  public void publishedPlayerUpdate(final P player, final boolean status) {
    //  Ignore - if they are logged out, they will refresh on login
  }

  @Override
  public void publishedGameUpdateToPlayer(
      final P player,
      final IMPL game,
      final boolean published) {
    if (pushProperties.isEnabled() &&
        player.getRegisteredDevices()
            .stream()
            .anyMatch(d -> d.getLastRegistered().compareTo(registeredCutoff) > 0)) {
      GamePublicationTracker<ID> tracker = new GamePublicationTracker<>();
      tracker.setPid(player.getId());
      tracker.setGid(game.getId());
      Boolean was;
      Boolean is;
      if (published) {
        logger.trace("Forcing published status on " + tracker);
        //noinspection ConstantConditions
        was = trackingMap.put(tracker, published);
      } else {
        logger.trace("putIfAbsent publish status on " + tracker);
        //noinspection ConstantConditions
        was = trackingMap.putIfAbsent(tracker, published);
      }

      if (logger.isTraceEnabled()) {
        is = trackingMap.get(tracker);
        logger.trace("Was " + was + ", is " + is);
      }

    }

  }
}
