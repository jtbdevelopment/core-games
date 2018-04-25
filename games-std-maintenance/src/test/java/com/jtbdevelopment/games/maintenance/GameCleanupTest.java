package com.jtbdevelopment.games.maintenance;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Date: 8/18/15 Time: 10:50 PM
 */
public class GameCleanupTest {

  private AbstractGameRepository gameRepository = Mockito.mock(AbstractGameRepository.class);
  private GameCleanup gameCleanup = new GameCleanup(gameRepository);

  @Test
  public void testDeleteOlderGames() {
    Instant start = ZonedDateTime.now(ZoneId.of("GMT")).minusDays(60).toInstant();
    gameCleanup.deleteOlderGames();
    ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
    verify(gameRepository).deleteByCreatedLessThan(captor.capture());
    assertTrue(start.compareTo(captor.getValue()) <= 0);
    assertTrue(start.plusSeconds(60).compareTo(captor.getValue()) > 0);

  }
}
