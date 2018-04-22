package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.MultiPlayerGame

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

import static com.jtbdevelopment.games.GameCoreTestCase.*

/**
 * Date: 12/8/14
 * Time: 7:11 PM
 */
class GamePublisherImplTest extends GroovyTestCase {
    GamePublisherImpl publisher = new GamePublisherImpl()

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

        MultiPlayerGame g1 = makeSimpleMPGame("1")
        MultiPlayerGame g2 = makeSimpleMPGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        assert publisher.subscribers == null
        publisher.publish(g1, p2)
        publisher.publish(g2, p1)
        publisher.service.shutdown()

        //  Not crashing is success
    }

    void testPublishWithDefaultInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        CountDownLatch latch = new CountDownLatch(4)
        def games1 = [] as Set, games2 = [] as Set
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games1.add(g)
                        players1.add(p)
                        latch.countDown()
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games2.add(g)
                        players2.add(p)
                        latch.countDown()
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        MultiPlayerGame g1 = makeSimpleMPGame("1")
        MultiPlayerGame g2 = makeSimpleMPGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2)
        publisher.publish(g2, p1)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert games1 == [g1, g2] as Set
        assert games2 == [g1, g2] as Set
        assert players1 == [p2, p1] as Set
        assert players2 == [p2, p1] as Set
    }

    void testPublishWithTrueInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()
        CountDownLatch latch = new CountDownLatch(4)

        def games1 = [] as Set, games2 = [] as Set
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games1.add(g)
                        players1.add(p)
                        latch.countDown()
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games2.add(g)
                        players2.add(p)
                        latch.countDown()
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        MultiPlayerGame g1 = makeSimpleMPGame("1")
        MultiPlayerGame g2 = makeSimpleMPGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2, true)
        publisher.publish(g2, p1, true)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert games1 == [g1, g2] as Set
        assert games2 == [g1, g2] as Set
        assert players1 == [p2, p1] as Set
        assert players2 == [p2, p1] as Set
    }

    void testPublishWithFalseInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        CountDownLatch latch = new CountDownLatch(4)

        def games1 = [] as Set, games2 = [] as Set
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assertFalse iS
                        games1.add(g)
                        players1.add(p)
                        latch.countDown()
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assertFalse iS
                        games2.add(g)
                        players2.add(p)
                        latch.countDown()
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        MultiPlayerGame g1 = makeSimpleMPGame("1")
        MultiPlayerGame g2 = makeSimpleMPGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2, false)
        publisher.publish(g2, p1, false)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert games1 == [g1, g2] as Set
        assert games2 == [g1, g2] as Set
        assert players1 == [p2, p1] as Set
        assert players2 == [p2, p1] as Set
    }


    void testPublishWithException() {
        publisher.threads = 2
        publisher.setUp()
        CountDownLatch latch = new CountDownLatch(2)

        def games1 = [] as Set, games2 = [] as Set
        def players1 = [] as Set, players2 = [] as Set
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assertFalse iS
                        games1.add(g)
                        players1.add(p)
                        latch.countDown()
                        throw new RuntimeException('x')
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assertFalse iS
                        games2.add(g)
                        players2.add(p)
                        latch.countDown()
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        MultiPlayerGame g1 = makeSimpleMPGame("1")
        MultiPlayerGame g2 = makeSimpleMPGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2, false)
        latch.await(1, TimeUnit.SECONDS)
        latch = new CountDownLatch(2)
        publisher.publish(g2, p1, false)
        latch.await(1, TimeUnit.SECONDS)
        publisher.service.shutdown()
        assert games1 == [g1, g2] as Set
        assert games2 == [g1, g2] as Set
        assert players1 == [p2, p1] as Set
        assert players2 == [p2, p1] as Set
    }

}
