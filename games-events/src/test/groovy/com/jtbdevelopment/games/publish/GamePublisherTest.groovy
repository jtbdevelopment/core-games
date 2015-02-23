package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.players.Player

import java.util.concurrent.Callable
import java.util.concurrent.ThreadPoolExecutor

/**
 * Date: 12/8/14
 * Time: 7:11 PM
 */
class GamePublisherTest extends GameCoreTestCase {
    GamePublisher publisher = new GamePublisher()

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

        Game g1 = makeSimpleGame("1")
        Game g2 = makeSimpleGame("2")
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

        def games = []
        def players = []
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games.add(g)
                        players.add(p)
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games.add(g)
                        players.add(p)
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        Game g1 = makeSimpleGame("1")
        Game g2 = makeSimpleGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2)
        publisher.publish(g2, p1)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert games == [g1, g1, g2, g2]
        assert players == [p2, p2, p1, p1]
    }

    void testPublishWithTrueInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def games = []
        def players = []
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games.add(g)
                        players.add(p)
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assert iS
                        games.add(g)
                        players.add(p)
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        Game g1 = makeSimpleGame("1")
        Game g2 = makeSimpleGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2, true)
        publisher.publish(g2, p1, true)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert games == [g1, g1, g2, g2]
        assert players == [p2, p2, p1, p1]
    }

    void testPublishWithFalseInitiatingServer() {
        publisher.threads = 1
        publisher.setUp()

        def games = []
        def players = []
        def gl1 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assertFalse iS
                        games.add(g)
                        players.add(p)
                },
        ] as GameListener
        def gl2 = [
                gameChanged: {
                    Game g, Player p, boolean iS ->
                        assertFalse iS
                        games.add(g)
                        players.add(p)
                },
        ] as GameListener

        publisher.subscribers = [gl1, gl2]

        Game g1 = makeSimpleGame("1")
        Game g2 = makeSimpleGame("2")
        Player p1 = PONE
        Player p2 = PTWO

        publisher.publish(g1, p2, false)
        publisher.publish(g2, p1, false)
        Thread.sleep(1000);
        publisher.service.shutdown()
        assert games == [g1, g1, g2, g2]
        assert players == [p2, p2, p1, p1]
    }


}
