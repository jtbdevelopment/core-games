package com.jtbdevelopment.games.publish;

import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 12/8/14 Time: 6:40 PM
 */
@Component
@Lazy
public class GamePublisherImpl implements GamePublisher<Game> {

  private static final Logger logger = LoggerFactory.getLogger(GamePublisherImpl.class);
  @Autowired(required = false)
  protected List<GameListener> subscribers;
  @Value("${publishing.threads:10}")
  protected int threads;
  private ExecutorService service;

  @PostConstruct
  public void setUp() {
    service = Executors.newFixedThreadPool(threads);
  }

  public Game publish(final Game game, final Player initiatingPlayer) {
    return publish(game, initiatingPlayer, true);
  }

  public Game publish(final Game game, final Player initiatingPlayer,
      final boolean initiatingServer) {
    service.execute(() -> {
      if (subscribers != null) {
        subscribers.forEach(subscriber -> {
          try {
            subscriber.gameChanged(game, initiatingPlayer, initiatingServer);
          } catch (Throwable e) {
            logger.error("Error publishing!", e);
          }
        });
      }

    });
    return game;
  }
}
