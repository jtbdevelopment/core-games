package com.jtbdevelopment.games.websocket

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.MultiPlayerGameMasker
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.cpr.BroadcasterFactory

import java.util.concurrent.TimeUnit

/**
 * Date: 12/22/14
 * Time: 7:18 PM
 */
class AtmosphereListenerTest extends GameCoreTestCase {
    AtmosphereListener listener = new AtmosphereListener()

    //  Initiating Server Flag irrelevant here
    boolean initiatingServer = new Random().nextBoolean()

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        listener.stringToIDConverter = new StringToIDConverter<String>() {
            @Override
            String convert(final String source) {
                return source?.reverse()
            }
        }
        listener.threads = 5
        listener.retries = 3
        listener.retryPause = 100
        listener.setUp()
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
                            case LiveFeedService.PATH_ROOT + PONE.idAsString:
                                return null
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
        AtmosphereBroadcasterFactory factoryFactory = [
                getBroadcasterFactory: {
                    return factory
                }
        ] as AtmosphereBroadcasterFactory
        listener.broadcasterFactory = factoryFactory
        [PONE, PTWO, PTHREE, PFOUR].each {
            listener.playerChanged(it, initiatingServer)
        }
        listener.service.shutdown()
        listener.service.awaitTermination(10, TimeUnit.SECONDS)
        assert p2pub && p4pub
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
        listener.service.shutdown()
        listener.service.awaitTermination(10, TimeUnit.SECONDS)
        assert p2pub && p4pub
    }

    void testPublishGameToConnectedNonInitiatingPlayers() {
        MultiPlayerGame game = [
                getId: {
                    return 'An ID!'
                },
                getPlayers: {
                    [PONE, PTWO, PTHREE, PFOUR]
                }
        ] as MultiPlayerGame
        boolean p2pub = false;
        boolean p4pub = false;
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
        MultiPlayerGameMasker masker = [
                maskGameForPlayer: {
                    Game g, Player p ->
                        assert game.is(g)
                        if (p == PTWO) return mg2
                        if (p == PFOUR) return mg4
                        fail("Masking for unexpected player")
                }
        ] as MultiPlayerGameMasker
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
        AtmosphereBroadcasterFactory factoryFactory = [
                getBroadcasterFactory: {
                    return factory
                }
        ] as AtmosphereBroadcasterFactory
        listener.broadcasterFactory = factoryFactory
        listener.gameMasker = masker

        listener.gameChanged(game, PONE, initiatingServer)
        listener.service.shutdown()
        listener.service.awaitTermination(10, TimeUnit.SECONDS)
        assert p2pub && p4pub
    }
}
