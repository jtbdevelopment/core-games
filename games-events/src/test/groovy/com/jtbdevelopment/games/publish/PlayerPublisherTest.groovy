package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.players.Player

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

import static com.jtbdevelopment.games.GameCoreTestCase.PONE
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO

/**
 * Date: 2/6/15
 * Time: 7:02 PM
 */
class PlayerPublisherTest extends GroovyTestCase {
    PlayerPublisher publisher = new PlayerPublisher()

    void testCreatesExecutorService() {
        publisher.threads = 20
        publisher.setUp()
        def expectedResult = "RESULT"
        assert ((ThreadPoolExecutor) publisher.service).corePoolSize == 20
        assert publisher.service.submit(new Callable() {
            @Override
            Object call() throws Exception {
                Thread.sleep(100)
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

        CountDownLatch latch = new CountDownLatch(4)
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players1.add(p)
                        latch.countDown()
                },
        ] as PlayerListener
        def gl2 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players2.add(p)
                        latch.countDown()
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publish(PTWO)
        publisher.publish(PONE)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert players1 == [PTWO, PONE] as Set
        assert players2 == [PTWO, PONE] as Set
    }

    void testPublishTrueInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        CountDownLatch latch = new CountDownLatch(4)
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players1.add(p)
                        latch.countDown()
                },
        ] as PlayerListener
        def gl2 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assert iS
                        players2.add(p)
                        latch.countDown()
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publish(PTWO, true)
        publisher.publish(PONE, true)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert players1 == [PTWO, PONE] as Set
        assert players2 == [PTWO, PONE] as Set
    }

    void testPublishFalseInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        CountDownLatch latch = new CountDownLatch(4)
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assertFalse iS
                        players1.add(p)
                        latch.countDown()
                },
        ] as PlayerListener
        def gl2 = [
                playerChanged: {
                    Player p, boolean iS ->
                        assertFalse iS
                        players2.add(p)
                        latch.countDown()
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publish(PTWO, false)
        publisher.publish(PONE, false)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert players1 == [PTWO, PONE] as Set
        assert players2 == [PTWO, PONE] as Set
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
        CountDownLatch latch = new CountDownLatch(2)
        def gl1 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl1Called = true
                        latch.countDown()
                },
        ] as PlayerListener
        def gl2 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl2Called = true
                        latch.countDown()
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publishAll()
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert gl1Called
        assert gl2Called
    }

    void testPublishAllTrueInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        CountDownLatch latch = new CountDownLatch(4)
        def gl1Called = false, gl2Called = false
        def gl1 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl1Called = true
                        latch.countDown()
                },
        ] as PlayerListener
        def gl2 = [
                allPlayersChanged: {
                    boolean iS ->
                        assert iS
                        gl2Called = true
                        latch.countDown()
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publishAll(true)
        publisher.publishAll(true)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert gl1Called
        assert gl2Called
    }

    void testPublishAllFalseInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        CountDownLatch latch = new CountDownLatch(4)
        def gl1Called = false, gl2Called = false
        def gl1 = [
                allPlayersChanged: {
                    boolean iS ->
                        assertFalse iS
                        gl1Called = true
                        latch.countDown()
                },
        ] as PlayerListener
        def gl2 = [
                allPlayersChanged: {
                    boolean iS ->
                        assertFalse iS
                        gl2Called = true
                        latch.countDown()
                },
        ] as PlayerListener

        publisher.subscribers = [gl1, gl2]

        publisher.publishAll(false)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert gl1Called
        assert gl2Called
    }
}
