package com.jtbdevelopment.games.websocket

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory

import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.TimeUnit

/**
 * Date: 12/22/14
 * Time: 7:18 PM
 */
class AtmosphereListenerTest extends GameCoreTestCase {
    AtmosphereListener listener

    //  Initiating Server Flag irrelevant here
    //  randomly choose on each - should not affect results
    boolean initiatingServer = new Random().nextBoolean()

    @Override
    protected void setUp() throws Exception {
        listener = new AtmosphereListener()
        listener.stringToIDConverter = new StringToIDConverter<String>() {
            @Override
            String convert(final String source) {
                return source?.reverse()
            }
        }
        listener.threads = 10
        listener.retries = 3
        listener.retryPause = 1
        listener.setUp()
        listener.publicationListeners = [];
    }

    void testPublishPlayerToConnectedPlayer() {
        boolean p2pub = false;
        boolean p4pub = false;
        Broadcaster b2 = [
                broadcast: {
                    Object o ->
                        assert o in WebSocketMessage
                        assert o.messageType == WebSocketMessage.MessageType.Player
                        assert o.player.is(PTWO)
                        assert o.message == null
                        p2pub = true
                        null
                }
        ] as Broadcaster
        Broadcaster b4 = [
                broadcast: {
                    Object o ->
                        assert o in WebSocketMessage
                        assert o.messageType == WebSocketMessage.MessageType.Player
                        assert o.player.is(PFOUR)
                        assert o.message == null
                        p4pub = true
                        null
                }
        ] as Broadcaster
        BroadcasterFactory factory = [
                lookup: {
                    String id ->
                        switch (id) {
                            case LiveFeedService.PATH_ROOT + PTHREE.idAsString:
                            case LiveFeedService.PATH_ROOT + PONE.idAsString:
                                return null
                                break;
                            case LiveFeedService.PATH_ROOT + PTWO.idAsString:
                                return b2
                                break;
                            case LiveFeedService.PATH_ROOT + PFOUR.idAsString:
                                return b4
                                break;
                        }
                        fail("Not sure how we got here")
                }
        ] as BroadcasterFactory
        AtmosphereBroadcasterFactory factoryFactory = [
                getBroadcasterFactory: {
                    return factory
                }
        ] as AtmosphereBroadcasterFactory

        def successes = new ConcurrentSkipListSet<String>()
        def failures = new ConcurrentSkipListSet<String>()
        listener.publicationListeners.add([
                publishedPlayerUpdate: {
                    Player p, boolean status ->

                        if (status) successes.add(p.idAsString)
                        else failures.add(p.idAsString)
                        log.info(failures.toString())
                        log.info(successes.toString())
                }
        ] as WebSocketPublicationListener)
        listener.broadcasterFactory = factoryFactory
        [PONE, PTWO, PTHREE, PFOUR].each {
            listener.playerChanged(it, initiatingServer)
        }
        Thread.sleep(1)
        listener.service.shutdown()
        listener.service.awaitTermination(100, TimeUnit.SECONDS)
        assert p2pub && p4pub
        assert [PTWO.idAsString, PFOUR.idAsString] as Set == successes
        assert [PONE.idAsString, PTHREE.idAsString] as Set == failures
    }

    void testPublishRefreshPlayerToAllValidConnectedPlayers() {
        boolean p2pub = false;
        boolean p4pub = false;
        Broadcaster bJunk = [
                broadcast: {
                    Object o ->
                        fail('should not get here')
                },
                getID    : {
                    return '/livefeed/JUNK'
                }
        ] as Broadcaster
        Broadcaster b2 = [
                broadcast: {
                    Object o ->
                        assert o in WebSocketMessage
                        assert o.messageType == WebSocketMessage.MessageType.Player
                        assert o.player.is(PTWO)
                        assertNull o.message
                        assertNull o.game
                        p2pub = true
                        null
                },
                getID    : {
                    return '/livefeed/' + PTWO.idAsString
                }
        ] as Broadcaster
        Broadcaster b4 = [
                broadcast: {
                    Object o ->
                        assert o in WebSocketMessage
                        assert o.messageType == WebSocketMessage.MessageType.Player
                        assert o.player.is(PFOUR)
                        assertNull o.message
                        assertNull o.game
                        p4pub = true
                        null
                },
                getID    : {
                    return '/livefeed/' + PFOUR.idAsString
                }
        ] as Broadcaster
        BroadcasterFactory factory = [
                lookupAll: {
                    return [b2, b4, bJunk]
                }
        ] as BroadcasterFactory
        AtmosphereBroadcasterFactory factoryFactory = [
                getBroadcasterFactory: {
                    return factory
                }
        ] as AtmosphereBroadcasterFactory
        listener.broadcasterFactory = factoryFactory
        listener.playerRepository = [
                findOne: {
                    String id ->
                        if (id == PTWO.idAsString.reverse()) {
                            return PTWO
                        }
                        if (id == PFOUR.idAsString.reverse()) {
                            return PFOUR
                        }
                        if (id == 'JUNK'.reverse()) {
                            return null
                        }
                        fail('unknown id')
                }
        ] as AbstractPlayerRepository
        listener.allPlayersChanged(initiatingServer)
        Thread.sleep(1)
        listener.service.shutdown()
        listener.service.awaitTermination(100, TimeUnit.SECONDS)
        assert p2pub && p4pub
    }

    void testPublishGameToConnectedNonInitiatingPlayers() {
        MultiPlayerGame game = [
                getId     : {
                    return 'An ID!'
                },
                getPlayers: {
                    [PONE, PTWO, PTHREE, PFOUR]
                }
        ] as MultiPlayerGame
        boolean p2pub = false
        boolean p4pub = false
        MaskedMultiPlayerGame mg2 = [] as MaskedMultiPlayerGame
        MaskedMultiPlayerGame mg4 = [] as MaskedMultiPlayerGame
        Broadcaster b2 = [
                broadcast: {
                    Object o ->
                        assert o in WebSocketMessage
                        assert o.messageType == WebSocketMessage.MessageType.Game
                        assert o.game.is(mg2)
                        assert o.message == null
                        assert o.player == null
                        p2pub = true
                        null
                }
        ] as Broadcaster
        Broadcaster b4 = [
                broadcast: {
                    Object o ->
                        assert o in WebSocketMessage
                        assert o.messageType == WebSocketMessage.MessageType.Game
                        assert o.game.is(mg4)
                        assert o.message == null
                        assert o.player == null
                        p4pub = true
                        null
                }
        ] as Broadcaster
        GameMasker masker = [
                maskGameForPlayer: {
                    Game g, Player p ->
                        assert game.is(g)
                        if (p == PTWO) return mg2
                        if (p == PFOUR) return mg4
                        fail("Masking for unexpected player")
                }
        ] as GameMasker
        BroadcasterFactory factory = [
                lookup: {
                    String id ->
                        switch (id) {
                            case LiveFeedService.PATH_ROOT + PONE.idAsString:
                                fail("Should not be requesting PONE lookup")
                                break;
                            case LiveFeedService.PATH_ROOT + PTWO.idAsString:
                                return b2
                                break;
                            case LiveFeedService.PATH_ROOT + PTHREE.idAsString:
                                return null
                                break;
                            case LiveFeedService.PATH_ROOT + PFOUR.idAsString:
                                return b4
                                break;
                        }
                        fail("Not sure how we got here")
                }
        ] as BroadcasterFactory

        def successes = new ConcurrentSkipListSet<String>()
        def failures = new ConcurrentSkipListSet<String>()
        listener.publicationListeners.add([
                publishedGameUpdateToPlayer: {
                    Player p, MultiPlayerGame g, boolean status ->
                        assert g.is(game)
                        if (status) successes.add(p.idAsString)
                        else failures.add(p.idAsString)
                        log.info(failures.toString())
                        log.info(successes.toString())
                }
        ] as WebSocketPublicationListener)

        AtmosphereBroadcasterFactory factoryFactory = [
                getBroadcasterFactory: {
                    return factory
                }
        ] as AtmosphereBroadcasterFactory
        listener.broadcasterFactory = factoryFactory
        listener.gameMasker = masker

        listener.gameChanged(game, PONE, initiatingServer)
        listener.service.shutdown()
        listener.service.awaitTermination(100, TimeUnit.SECONDS)
        assert p2pub && p4pub
        assert [PTWO.idAsString, PFOUR.idAsString] as Set == successes
        assert [PTHREE.idAsString] as Set == failures
    }
}
