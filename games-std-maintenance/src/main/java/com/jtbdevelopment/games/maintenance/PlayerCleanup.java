package com.jtbdevelopment.games.maintenance;

import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.SystemPlayer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * Date: 8/18/15 Time: 10:46 PM
 *
 * TODO - perhaps we should archive them in the future and/or move them to a compressed collection
 */
@Component
public class PlayerCleanup {

  private static final Logger logger = LoggerFactory.getLogger(PlayerCleanup.class);
  private static final ZoneId GMT = ZoneId.of("GMT");
  private static final int DAYS_BACK = 90;
  @Autowired
  protected AbstractPlayerRepository playerRepository;
  @Autowired(required = false)
  protected AbstractUsersConnectionRepository usersConnectionRepository;

  public void deleteInactivePlayers() {
    ZonedDateTime cutoff = ZonedDateTime.now(GMT).minusDays(DAYS_BACK);
    logger.info("Deleting players not logged in since " + cutoff);

    List<Player> byLastLoginLessThan = playerRepository.findByLastLoginLessThan(cutoff.toInstant());
    List<Player<?>> playersToDelete = byLastLoginLessThan
        .stream()
        .filter(x -> !(x instanceof ManualPlayer || x instanceof SystemPlayer))
        .collect(Collectors.toList());
    logger.info("Found " + playersToDelete.size() + " to cleanup.");
    playersToDelete.forEach(p -> {
      if (usersConnectionRepository != null) {
        final ConnectionRepository userSpecificRepository = usersConnectionRepository
            .createConnectionRepository(p.getIdAsString());
        if (userSpecificRepository != null) {
          final MultiValueMap<String, Connection<?>> connections = userSpecificRepository
              .findAllConnections();
          connections.keySet().stream().forEach(provider -> {
            List<Connection<?>> listOfConnections = connections.get(provider);
            listOfConnections.forEach(connection -> {
              userSpecificRepository.removeConnection(connection.getKey());
            });
          });
        }
      }

      playerRepository.delete(p);
    });

    logger.info("Cleanup completed");
  }
}
