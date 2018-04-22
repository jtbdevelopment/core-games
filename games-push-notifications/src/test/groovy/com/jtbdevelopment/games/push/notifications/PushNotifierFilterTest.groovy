package com.jtbdevelopment.games.push.notifications

import com.hazelcast.core.EntryEvent
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.push.PushWorthyFilter
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.stringimpl.StringMPGame
import com.jtbdevelopment.games.stringimpl.StringPlayer

import java.util.concurrent.ConcurrentHashMap

/**
 * Date: 10/11/2015
 * Time: 8:36 PM
 */
class PushNotifierFilterTest extends GroovyTestCase {
    PushNotifierFilter filter = new PushNotifierFilter()

    void testSetup() {
        IMap map = [] as IMap
        filter.hazelcastInstance = [
                getMap: {
                    String name ->
                        assert name == PushNotifierFilter.PLAYER_PUSH_TRACKING_MAP
                        return map
                }
        ] as HazelcastInstance

        filter.setup()
        assert filter.recentlyPushedPlayers.is(map)
    }

    void testDoesNothingIfValueOnEvictionIsTrue() {
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, null, true, null))
    }

    void testDoesNothingIfRecentlyPushedToPlayer() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        filter.recentlyPushedPlayers.put(pid, pid)
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid), false, null))
    }

    void testDoesNothingIfUnableToLoadPlayer() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"
        filter.playerRepository = [
                findById: {
                    Serializable id ->
                        assert pid == id
                        return Optional.empty()
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findById: {
                    Serializable id ->
                        assert gid == id
                        return Optional.of(new StringMPGame())
                }
        ] as AbstractMultiPlayerGameRepository
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false, null))
    }

    void testDoesNothingIfUnableToLoadGame() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"
        filter.playerRepository = [
                findById: {
                    Serializable id ->
                        assert pid == id
                        return Optional.of(new StringPlayer())
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findById: {
                    Serializable id ->
                        assert gid == id
                        return Optional.empty()
                }
        ] as AbstractMultiPlayerGameRepository
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false, null))
    }

    void testDoesNothingIfNotPushWorthy() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"

        def game = new StringMPGame()
        def player = new StringPlayer()
        filter.playerRepository = [
                findById: {
                    Serializable id ->
                        assert pid == id
                        return Optional.of(player)
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findById: {
                    Serializable id ->
                        assert gid == id
                        return Optional.of(game)
                }
        ] as AbstractMultiPlayerGameRepository
        filter.filter = [
                shouldPush: {
                    Player p, MultiPlayerGame g ->
                        assert p.is(player)
                        assert g.is(game)
                        return false
                }
        ] as PushWorthyFilter
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false, null))
    }

    void testDoesPushIfPushWorthy() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"

        def game = new StringMPGame()
        def player = new StringPlayer()
        filter.playerRepository = [
                findById: {
                    Serializable id ->
                        assert pid == id
                        return Optional.of(player)
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findById: {
                    Serializable id ->
                        assert gid == id
                        return Optional.of(game)
                }
        ] as AbstractMultiPlayerGameRepository
        filter.filter = [
                shouldPush: {
                    Player p, MultiPlayerGame g ->
                        assert p.is(player)
                        assert g.is(game)
                        return true
                }
        ] as PushWorthyFilter
        boolean pushed = false
        filter.pushNotifier = [
                notifyPlayer: {
                    Player p, MultiPlayerGame g ->
                        assert p.is(player)
                        assert g.is(game)
                        pushed = true
                }
        ] as PushNotifier
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false, null))
        assert pushed
    }
}
