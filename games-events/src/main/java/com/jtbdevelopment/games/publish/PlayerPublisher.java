package com.jtbdevelopment.games.publish;

import com.jtbdevelopment.games.players.Player;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 12/8/14 Time: 6:40 PM
 */
@Component
public class PlayerPublisher {

  private static final Logger logger = LoggerFactory.getLogger(PlayerPublisher.class);
  private List<PlayerListener> subscribers;
  final ExecutorService service;

  public PlayerPublisher(
      @Value("${publishing.threads:10}") final int threads,
      final List<PlayerListener> subscribers) {
    this.service = Executors.newFixedThreadPool(threads);
    this.subscribers = subscribers;
  }

  public void publish(final Player player, final boolean initiatingServer) {
    service.submit(() -> {
      if (subscribers != null) {
        subscribers.forEach(subscriber -> {
          try {
            subscriber.playerChanged(player, initiatingServer);
          } catch (Exception e) {
            logger.warn("Failed to publish player", e);
          }
        });
      }
    });
  }

  public void publish(final Player player) {
    publish(player, true);
  }

  public void publishAll(boolean initiatingServer) {
    service.submit(() -> {
      if (subscribers != null) {
        subscribers.forEach(subscriber -> {
          try {
            subscriber.allPlayersChanged(initiatingServer);
          } catch (Exception e) {
            logger.warn("Failed to publish player", e);
          }
        });
      }
    });
  }

  public void publishAll() {
    publishAll(true);
  }
}
