package com.jtbdevelopment.games.datagrid.hazelcast.cluster

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.ITopic
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.PlayerPublisher
import com.jtbdevelopment.games.publish.cluster.ClusterMessage
import com.jtbdevelopment.games.state.MultiPlayerGame
import org.springframework.test.util.ReflectionTestUtils

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static com.jtbdevelopment.games.GameCoreTestCase.*

/**
 * Date: 3/4/15
 * Time: 7:36 AM
 */
class UpdatesFromClusterListenerTest extends GroovyTestCase {
    static HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance()
    static HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance()

    UpdatesFromClusterListener listener1 = new UpdatesFromClusterListener()
    UpdatesFromClusterListener listener2 = new UpdatesFromClusterListener()

    CountDownLatch latch
    List<Map> listener1Actions = []
    List<Map> listener2Actions = []
    MultiPlayerGame game1 = makeSimpleMPGame('X')
    MultiPlayerGame game2 = makeSimpleMPGame('Y')
    GamePublisher gamePublisher1 = [
            publish: {
                MultiPlayerGame g, Player p, boolean b ->
                    assertFalse b
                    listener1Actions.add([game: [(g): p]])
                    latch.countDown()
            }
    ] as GamePublisher
    GamePublisher gamePublisher2 = [
            publish: {
                MultiPlayerGame g, Player p, boolean b ->
                    assertFalse b
                    listener2Actions.add([game: [(g): p]])
                    latch.countDown()
            }
    ] as GamePublisher
    PlayerPublisher playerPublisher1 = [
            clearAlls : 0,
            publishAll: {
                boolean b ->
                    assertFalse b
                    listener1Actions.add([allplayer: true])
                    latch.countDown()
            },
            publish   : {
                Player p, boolean b ->
                    assertFalse b
                    listener1Actions.addAll(player: p)
                    latch.countDown()
            }
    ] as PlayerPublisher
    PlayerPublisher playerPublisher2 = [
            clearAlls : {

            },
            publishAll: {
                boolean b ->
                    assertFalse b
                    listener2Actions.add([allplayer: true])
                    latch.countDown()
            },
            publish   : {
                Player p, boolean b ->
                    assertFalse b
                    listener2Actions.addAll(player: p)
                    latch.countDown()
            }
    ] as PlayerPublisher
    def gameRepository = [
            findById: {
                String id ->
                    switch (id) {
                        case game1.idAsString.reverse():
                            return Optional.of(game1)
                        case game2.idAsString.reverse():
                            return Optional.of(game2)
                    }
                    return null
            }
    ] as AbstractMultiPlayerGameRepository
    def playerRepository = [
            findById: {
                String id ->
                    switch (id) {
                        case PONE.idAsString.reverse():
                            return Optional.of(PONE)
                        case PTWO.idAsString.reverse():
                            return Optional.of(PTWO)
                        case PTHREE.idAsString.reverse():
                            return Optional.of(PTHREE)
                        case PFOUR.idAsString.reverse():
                            return Optional.of(PFOUR)
                    }
                    return null
            }
    ] as AbstractPlayerRepository
    ITopic topicFrom1
    ITopic topicFrom2
    def static converter = new StringToIDConverter<String>() {
        @Override
        String convert(final String source) {
            return source?.reverse()
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        topicFrom1 = hazelcastInstance1.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC)
        topicFrom2 = hazelcastInstance2.getTopic(UpdatesToClusterPublisher.PUB_SUB_TOPIC)
        ReflectionTestUtils.setField(listener2, "stringToIDConverter", converter)
        ReflectionTestUtils.setField(listener1, "stringToIDConverter", converter)
        listener1.playerPublisher = playerPublisher1
        listener2.playerPublisher = playerPublisher2
        listener1.gamePublisher = gamePublisher1
        listener2.gamePublisher = gamePublisher2
        listener1.gameRepository = gameRepository
        listener2.gameRepository = gameRepository
        listener1.hazelcastInstance = hazelcastInstance1
        listener1.setup()
        listener2.hazelcastInstance = hazelcastInstance2
        listener2.setup()
    }

    void testClearPlayers() {
        latch = new CountDownLatch(3)
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.AllPlayersUpdate))
        topicFrom2.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.AllPlayersUpdate))
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.AllPlayersUpdate))
        latch.await(1, TimeUnit.SECONDS)
        assert listener1Actions == [[allplayer: true]]
        assert listener2Actions == [[allplayer: true], [allplayer: true]]
    }

    void testUpdatePlayer() {
        latch = new CountDownLatch(3)

        listener2.playerRepository = playerRepository
        listener1.playerRepository = playerRepository
        listener2.stringToIDConverter = converter
        listener1.stringToIDConverter = converter
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.PlayerUpdate, playerId: PONE.idAsString))
        topicFrom2.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.PlayerUpdate, playerId: PTWO.idAsString))
        //  Bad one should be skipped
        topicFrom2.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.PlayerUpdate, playerId: 'JUNK'))
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.PlayerUpdate, playerId: PFOUR.idAsString))
        latch.await(1, TimeUnit.SECONDS)
        assert listener1Actions == [[player: PTWO]]
        assert listener2Actions == [[player: PONE], [player: PFOUR]]
    }

    void testUpdateGames() {
        latch = new CountDownLatch(3)

        listener2.playerRepository = playerRepository
        listener1.playerRepository = playerRepository
        listener2.stringToIDConverter = converter
        listener1.stringToIDConverter = converter
        //  Bad one should be skipped
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate, playerId: PONE.idAsString, gameId: 'JUNK'))
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate, playerId: PONE.idAsString, gameId: game1.idAsString))
        topicFrom2.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate, playerId: PTWO.idAsString, gameId: game1.idAsString))
        //  Bad one should be skipped
        topicFrom2.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate, playerId: 'JUNK', gameId: game2.idAsString))
        topicFrom1.publish(new ClusterMessage(clusterMessageType: ClusterMessage.ClusterMessageType.GameUpdate, playerId: PFOUR.idAsString, gameId: game2.idAsString))
        latch.await(1, TimeUnit.SECONDS)
        assert listener1Actions == [[game: [(game1): PTWO]]]
        assert listener2Actions == [[game: [(game1): PONE]], [game: [(game2): PFOUR]]]

    }
}
