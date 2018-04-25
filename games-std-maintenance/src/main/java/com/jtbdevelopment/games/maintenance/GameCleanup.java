package com.jtbdevelopment.games.maintenance;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Date: 8/18/15 Time: 10:46 PM
 *
 * TODO - perhaps we should archive them in the future and/or move them to a compressed collection
 */
@Component
public class GameCleanup {

  private static final Logger logger = LoggerFactory.getLogger(GameCleanup.class);
  private static final ZoneId GMT = ZoneId.of("GMT");
  private static final int DAYS_BACK = 60;
  protected final AbstractGameRepository gameRepository;

  public GameCleanup(final AbstractGameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  public void deleteOlderGames() {
    ZonedDateTime cutoff = ZonedDateTime.now(GMT).minusDays(DAYS_BACK);
    logger.info("Deleting games created before " + cutoff);
    logger.info(
        "Deleted games count = " + gameRepository.deleteByCreatedLessThan(cutoff.toInstant()));
  }
}
