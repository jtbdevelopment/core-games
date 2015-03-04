package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.games.MultiPlayerGame
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Date: 12/8/14
 * Time: 6:40 PM
 */
@Component
@Lazy
@CompileStatic
class GamePublisher {
    @Autowired(required = false)
    List<GameListener> subscribers

    @Value('${publishing.threads:10}')
    int threads

    ExecutorService service;

    //  Returns game primarily to allow easy chaining
    Game publish(final MultiPlayerGame game, final Player initiatingPlayer, boolean initiatingServer = true) {
        service.submit(new Runnable() {
            @Override
            void run() {
                if (subscribers != null) {
                    subscribers.each {
                        GameListener listener ->
                            listener.gameChanged(game, initiatingPlayer, initiatingServer)
                    }
                }
            }
        })
        game
    }


    @PostConstruct
    public void setUp() {
        service = Executors.newFixedThreadPool(threads)
    }
}
