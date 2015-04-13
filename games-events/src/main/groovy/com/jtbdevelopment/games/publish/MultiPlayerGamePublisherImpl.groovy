package com.jtbdevelopment.games.publish

import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class MultiPlayerGamePublisherImpl implements GamePublisher<MultiPlayerGame> {
    private static final Logger logger = LoggerFactory.getLogger(MultiPlayerGamePublisherImpl.class)

    @Autowired(required = false)
    List<GameListener> subscribers

    @Value('${publishing.threads:10}')
    int threads

    ExecutorService service;

    MultiPlayerGame publish(final MultiPlayerGame game, final Player initiatingPlayer) {
        return publish(game, initiatingPlayer, true)
    }

    //  Returns game primarily to allow easy chaining
    MultiPlayerGame publish(final MultiPlayerGame game, final Player initiatingPlayer, boolean initiatingServer) {
        service.submit(new Runnable() {
            @Override
            void run() {
                if (subscribers != null) {
                    subscribers.each {
                        GameListener listener ->
                            try {
                                listener.gameChanged(game, initiatingPlayer, initiatingServer)
                            } catch (Throwable e) {
                                logger.error('Error publishing!', e)
                            }
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
