package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.players.Player

import java.util.concurrent.Callable
import java.util.concurrent.ThreadPoolExecutor

/**
 * Date: 2/6/15
 * Time: 7:02 PM
 */
class PlayerPublisherTest extends GameCoreTestCase {
    PlayerPublisher publisher = new PlayerPublisher()

    void testCreatesExecutorService() {
        publisher.threads = 20
        publisher.setUp()
        def expectedResult = "RESULT"
        assert ((ThreadPoolExecutor) publisher.service).corePoolSize == 20
        assert publisher.service.submit(new Callable() {
            @Override
            Object call() throws Exception {
                Thread.sleep(1000)
                return expectedResult
            }
        }).get() == expectedResult
        publisher.service.shutdownNow()
    }

    void testPublishWithNoListeners() {
        publisher.threads = 1
        publisher.setUp()

        assert publisher.subscribers == null
        publisher.publish(PONE)
        publisher.publish(PTWO)
        publisher.service.shutdown()

        //  Not crashing is success
    }

    void testPublishDefaultInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def players = []
        def gl1 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players.add(p)
                },
        ] as PlayerListener
        def gl2 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players.add(p)
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publish(PTWO)
        publisher.publish(PONE)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert players == [PTWO, PTWO, PONE, PONE]
    }

    void testPublishTrueInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def players = []
        def gl1 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players.add(p)
                },
        ] as PlayerListener
        def gl2 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players.add(p)
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publish(PTWO, true)
        publisher.publish(PONE, true)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert players == [PTWO, PTWO, PONE, PONE]
    }

    void testPublishFalseInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def players = []
        def gl1 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assertFalse iS
                        players.add(p)
                },
        ] as PlayerListener
        def gl2 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assertFalse iS
                        players.add(p)
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publish(PTWO, false)
        publisher.publish(PONE, false)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert players == [PTWO, PTWO, PONE, PONE]
    }

    void testPublishAllWithNoListeners() {
        publisher.threads = 1
        publisher.setUp()

        assert publisher.subscribers == null
        publisher.publishAll()
        publisher.service.shutdown()

        //  Not crashing is success
    }

    void testPublishAllDefaultInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def gl1Called = false, gl2Called = false
        def gl1 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl1Called = true
                },
        ] as PlayerListener
        def gl2 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl2Called = true
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publishAll()
        publisher.publishAll()
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert gl1Called
        assert gl2Called
    }

    void testPublishAllTrueInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def gl1Called = false, gl2Called = false
        def gl1 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl1Called = true
                },
        ] as PlayerListener
        def gl2 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl2Called = true
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publishAll(true)
        publisher.publishAll(true)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert gl1Called
        assert gl2Called
    }

    void testPublishAllFalseInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def gl1Called = false, gl2Called = false
        def gl1 = [
                allPlayersChanged: {
                    boolean iS ->
                        assertFalse iS
                        gl1Called = true
                },
        ] as PlayerListener
        def gl2 = [
                allPlayersChanged: {
                    boolean iS ->
                        assertFalse iS
                        gl2Called = true
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publishAll(false)
        publisher.publishAll(false)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert gl1Called
        assert gl2Called
    }
}
