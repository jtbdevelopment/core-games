package com.jtbdevelopment.games.publish;

import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 12/8/14 Time: 6:40 PM
 */
@Component
@Lazy
public class GamePublisherImpl<IMPL extends Game<?, ?, ?>> implements GamePublisher<IMPL> {

  private static final Logger logger = LoggerFactory.getLogger(GamePublisherImpl.class);
  final ExecutorService service;
  private final List<GameListener<IMPL>> subscribers;

  //  Keep generic
  GamePublisherImpl(
      @Value("${publishing.threads:10}") final int threads,
      final List<GameListener> subscribers) {
    service = Executors.newFixedThreadPool(threads);
    //noinspection unchecked
    this.subscribers = subscribers.stream().map(s -> (GameListener<IMPL>) s)
        .collect(Collectors.toList());
  }

  public IMPL publish(final IMPL game, final Player initiatingPlayer) {
    return publish(game, initiatingPlayer, true);
  }

  public IMPL publish(final IMPL game, final Player initiatingPlayer,
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
