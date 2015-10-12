package com.jtbdevelopment.games.push

import com.hazelcast.core.EntryEvent
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame

import java.util.concurrent.ConcurrentHashMap

/**
 * Date: 10/11/2015
 * Time: 8:36 PM
 */
class PushNotifierFilterTest extends GameCoreTestCase {
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
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, null, true))
    }

    void testDoesNothingIfRecentlyPushedToPlayer() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        filter.recentlyPushedPlayers.put(pid, pid)
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid), false))
    }

    void testDoesNothingIfUnableToLoadPlayer() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"
        filter.playerRepository = [
                findOne: {
                    Serializable id ->
                        assert pid == id
                        return null
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findOne: {
                    Serializable id ->
                        assert gid == id
                        return new GameCoreTestCase.StringMPGame()
                }
        ] as AbstractMultiPlayerGameRepository
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false))
    }

    void testDoesNothingIfUnableToLoadGame() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"
        filter.playerRepository = [
                findOne: {
                    Serializable id ->
                        assert pid == id
                        return new GameCoreTestCase.StringPlayer()
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findOne: {
                    Serializable id ->
                        assert gid == id
                        return null
                }
        ] as AbstractMultiPlayerGameRepository
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false))
    }

    void testDoesNothingIfNotPushWorthy() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"

        def game = new GameCoreTestCase.StringMPGame()
        def player = new GameCoreTestCase.StringPlayer()
        filter.playerRepository = [
                findOne: {
                    Serializable id ->
                        assert pid == id
                        return player
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findOne: {
                    Serializable id ->
                        assert gid == id
                        return game
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
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false))
    }

    void testDoesPushIfPushWorthy() {
        filter.recentlyPushedPlayers = new ConcurrentHashMap<>()
        Serializable pid = "XY12"
        Serializable gid = "113"

        def game = new GameCoreTestCase.StringMPGame()
        def player = new GameCoreTestCase.StringPlayer()
        filter.playerRepository = [
                findOne: {
                    Serializable id ->
                        assert pid == id
                        return player
                }
        ] as AbstractPlayerRepository
        filter.gameRepository = [
                findOne: {
                    Serializable id ->
                        assert gid == id
                        return game
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
        filter.entryEvicted(new EntryEvent<GamePublicationTracker, Boolean>("TEST", null, 0, new GamePublicationTracker(pid: pid, gid: gid), false))
        assert pushed
    }
}
