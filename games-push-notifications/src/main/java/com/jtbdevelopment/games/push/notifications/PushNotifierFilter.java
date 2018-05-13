package com.jtbdevelopment.games.push.notifications;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.push.PushWorthyFilter;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 10/10/2015 Time: 4:22 PM
 */
@Component
public class PushNotifierFilter<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>> implements
    EntryEvictedListener<GamePublicationTracker<ID>, Boolean> {

  public static final String PLAYER_PUSH_TRACKING_MAP = "PUSH_PLAYER_TRACKING_SET";
  private static final Logger logger = LoggerFactory.getLogger(PushNotifierFilter.class);
  final ConcurrentMap<ID, ID> recentlyPushedPlayers;
  private final PushWorthyFilter<ID, FEATURES, IMPL, P> filter;
  private final AbstractMultiPlayerGameRepository<ID, FEATURES, IMPL> gameRepository;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final PushNotifier<ID, P> pushNotifier;

  PushNotifierFilter(
      final HazelcastInstance hazelcastInstance,
      final PushWorthyFilter<ID, FEATURES, IMPL, P> filter,
      final AbstractMultiPlayerGameRepository<ID, FEATURES, IMPL> gameRepository,
      final AbstractPlayerRepository<ID, P> playerRepository,
      final PushNotifier<ID, P> pushNotifier) {
    this.filter = filter;
    this.gameRepository = gameRepository;
    this.playerRepository = playerRepository;
    this.pushNotifier = pushNotifier;
    recentlyPushedPlayers = hazelcastInstance.getMap(PLAYER_PUSH_TRACKING_MAP);
  }

  @Override
  public void entryEvicted(final EntryEvent<GamePublicationTracker<ID>, Boolean> event) {
    logger.trace("Evicting push check " + event);
    if (event.getOldValue()) {
      return;

    }

    logger.trace("Checking push for " + event.getKey() + ", value " + event.getValue());
    if (recentlyPushedPlayers.putIfAbsent(event.getKey().getPid(), event.getKey().getPid())
        == null) {
      logger.trace("Not pushed recently " + event.getKey());
      Optional<P> player = playerRepository.findById(event.getKey().getPid());
      Optional<IMPL> game = gameRepository.findById((event.getKey().getGid()));
      if (player.isPresent() && game.isPresent() && filter.shouldPush(player.get(), game.get())) {
        logger.trace("Deemed push worthy " + event.getKey());
        pushNotifier.notifyPlayer(player.get(), game.get());
      }

    }

    logger.trace("Completed push check for " + event.getKey());
  }
}
