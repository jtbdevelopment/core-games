package com.jtbdevelopment.games.push.notifications

import com.hazelcast.core.EntryEvent
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.listener.EntryEvictedListener
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.push.PushWorthyFilter
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.concurrent.ConcurrentMap

/**
 * Date: 10/10/2015
 * Time: 4:22 PM
 */
@Component
@CompileStatic
class PushNotifierFilter implements EntryEvictedListener<GamePublicationTracker, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(PushNotifierFilter.class)

    static final String PLAYER_PUSH_TRACKING_MAP = "PUSH_PLAYER_TRACKING_SET"
    protected ConcurrentMap<Serializable, Serializable> recentlyPushedPlayers

    @Autowired
    HazelcastInstance hazelcastInstance

    @Autowired
    PushWorthyFilter filter

    @Autowired
    AbstractMultiPlayerGameRepository gameRepository
    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    PushNotifier pushNotifier

    @PostConstruct
    void setup() {
        recentlyPushedPlayers = hazelcastInstance.getMap(PLAYER_PUSH_TRACKING_MAP)
    }

    @Override
    void entryEvicted(final EntryEvent<GamePublicationTracker, Boolean> event) {
        if (event.value) {
            return
        }

        logger.trace('Checking push for ' + event.key)
        if (recentlyPushedPlayers.putIfAbsent(event.key.pid, event.key.pid) == null) {
            logger.trace('Not pushed recently ' + event.key)
            Player player = playerRepository.findOne(event.key.pid)
            MultiPlayerGame game = (MultiPlayerGame) gameRepository.findOne((event.key.gid))
            if (player && game && filter.shouldPush(player, game)) {
                logger.trace('Deemed push worthy ' + event.key)
                pushNotifier.notifyPlayer(player, game)
            }
        }
        logger.trace('Completed push check for ' + event.key)
    }
}
