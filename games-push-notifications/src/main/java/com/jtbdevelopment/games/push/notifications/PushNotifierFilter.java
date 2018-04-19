package com.jtbdevelopment.games.push.notifications;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.push.PushWorthyFilter;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 10/10/2015 Time: 4:22 PM
 */
@Component
public class PushNotifierFilter implements EntryEvictedListener<GamePublicationTracker, Boolean> {

  private static final Logger logger = LoggerFactory.getLogger(PushNotifierFilter.class);
  private static final String PLAYER_PUSH_TRACKING_MAP = "PUSH_PLAYER_TRACKING_SET";
  protected ConcurrentMap<Serializable, Serializable> recentlyPushedPlayers;
  @Autowired
  protected HazelcastInstance hazelcastInstance;
  @Autowired
  protected PushWorthyFilter filter;
  @Autowired
  protected AbstractMultiPlayerGameRepository gameRepository;
  @Autowired
  protected AbstractPlayerRepository playerRepository;
  @Autowired
  protected PushNotifier pushNotifier;

  public static String getPLAYER_PUSH_TRACKING_MAP() {
    return PLAYER_PUSH_TRACKING_MAP;
  }

  @PostConstruct
  public void setup() {
    recentlyPushedPlayers = hazelcastInstance.getMap(PLAYER_PUSH_TRACKING_MAP);
  }

  @Override
  public void entryEvicted(final EntryEvent<GamePublicationTracker, Boolean> event) {
    logger.trace("Evicting push check " + event);
    if (event.getOldValue().booleanValue()) {
      return;

    }

    logger.trace("Checking push for " + event.getKey() + ", value " + event.getValue());
    if (recentlyPushedPlayers.putIfAbsent(event.getKey().getPid(), event.getKey().getPid())
        == null) {
      logger.trace("Not pushed recently " + event.getKey());
      Optional<? extends Player> player = playerRepository.findById(event.getKey().getPid());
      Optional<? extends MultiPlayerGame> game = gameRepository.findById((event.getKey().getGid()));
      if (player.isPresent() && game.isPresent() && filter.shouldPush(player.get(), game.get())) {
        logger.trace("Deemed push worthy " + event.getKey());
        pushNotifier.notifyPlayer(player.get(), game.get());
      }

    }

    logger.trace("Completed push check for " + event.getKey());
  }
}
