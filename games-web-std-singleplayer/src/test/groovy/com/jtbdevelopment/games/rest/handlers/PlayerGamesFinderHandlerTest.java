package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.stringimpl.StringMaskedSPGame;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * Date: 12/4/2014 Time: 9:59 PM
 */
public class PlayerGamesFinderHandlerTest {

  private GameMasker gameMasker = Mockito.mock(GameMasker.class);
  private AbstractSinglePlayerGameRepository gameRepository = Mockito
      .mock(AbstractSinglePlayerGameRepository.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private Sort expectedSort = new Sort(Direction.DESC,
      Arrays.asList("lastUpdate", "created"));
  private Integer expectedPageSize = 20;
  private Integer expectedPage = 0;
  private PageRequest pageRequest = PageRequest.of(expectedPage, expectedPageSize, expectedSort);
  private PlayerGamesFinderHandler handler = new PlayerGamesFinderHandler(gameMasker);

  @Before
  public void setup() {
    handler.playerRepository = playerRepository;
    handler.gameRepository = gameRepository;
  }

  @Test
  public void testTest() {
    StringSPGame game1 = GameCoreTestCase.makeSimpleSPGame("1");
    StringSPGame game2 = GameCoreTestCase.makeSimpleSPGame("2");
    StringSPGame game3 = GameCoreTestCase.makeSimpleSPGame("3");
    StringMaskedSPGame masked1 = new StringMaskedSPGame();
    masked1.setId("1");
    StringMaskedSPGame masked2 = new StringMaskedSPGame();
    masked2.setId("2");
    StringMaskedSPGame masked3 = new StringMaskedSPGame();
    masked3.setId("3");
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.Challenged), Matchers.isA(Instant.class),
            Matchers.eq(pageRequest)))
        .thenReturn(Collections.singletonList(game1));
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.NextRoundStarted), Matchers.isA(Instant.class),
            Matchers.eq(pageRequest)))
        .thenReturn(Collections.singletonList(game2));
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.RoundOver), Matchers.isA(Instant.class),
            Matchers.eq(pageRequest)))
        .thenReturn(Collections.singletonList(game3));
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.Declined), Matchers.isA(Instant.class), Matchers.eq(pageRequest)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.Playing), Matchers.isA(Instant.class), Matchers.eq(pageRequest)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.Quit), Matchers.isA(Instant.class), Matchers.eq(pageRequest)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(Matchers.eq(PONE.getId()),
            Matchers.eq(GamePhase.Setup), Matchers.isA(Instant.class), Matchers.eq(pageRequest)))
        .thenReturn(Collections.emptyList());
    when(gameMasker.maskGameForPlayer(game1, PONE)).thenReturn(masked1);
    when(gameMasker.maskGameForPlayer(game2, PONE)).thenReturn(masked2);
    when(gameMasker.maskGameForPlayer(game3, PONE)).thenReturn(masked3);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));

    assertEquals(new HashSet<>(Arrays.asList(masked3, masked2, masked1)),
        new HashSet<>(handler.findGames(PONE.getId())));
  }
}
