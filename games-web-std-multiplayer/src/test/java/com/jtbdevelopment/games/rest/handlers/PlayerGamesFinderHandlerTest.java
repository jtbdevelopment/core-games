package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleMPGame;
import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleMaskedMPGame;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringMaskedMPGame;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * Date: 12/4/2014 Time: 9:59 PM
 */
public class PlayerGamesFinderHandlerTest {

  private static final PageRequest PAGE = PageRequest
      .of(0, 20, new Sort(Direction.DESC, Arrays.asList("lastUpdate", "created")));
  private GameMasker masker = mock(GameMasker.class);
  private AbstractPlayerRepository playerRepository = mock(AbstractPlayerRepository.class);
  private AbstractMultiPlayerGameRepository gameRepository = mock(
      AbstractMultiPlayerGameRepository.class);
  private PlayerGamesFinderHandler handler = new PlayerGamesFinderHandler(playerRepository,
      gameRepository, masker);

  @Test
  public void testTest() {
    StringMPGame game1 = makeSimpleMPGame("1");
    StringMPGame game2 = makeSimpleMPGame("2");
    StringMPGame game3 = makeSimpleMPGame("3");
    StringMaskedMPGame masked1 = makeSimpleMaskedMPGame("1");
    StringMaskedMPGame masked2 = makeSimpleMaskedMPGame("2");
    StringMaskedMPGame masked3 = makeSimpleMaskedMPGame("3");
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.Declined), isA(Instant.class), eq(PAGE)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.Playing), isA(Instant.class), eq(PAGE)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.Quit), isA(Instant.class), eq(PAGE)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.Setup), isA(Instant.class), eq(PAGE)))
        .thenReturn(Collections.emptyList());
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.Challenged), isA(Instant.class), eq(PAGE)))
        .thenReturn(Collections.singletonList(game1));
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.NextRoundStarted), isA(Instant.class),
            eq(PAGE))).thenReturn(Collections.singletonList(game2));
    when(gameRepository
        .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(eq(PONE.getId()),
            eq(GamePhase.RoundOver), isA(Instant.class), eq(PAGE)))
        .thenReturn(Collections.singletonList(game3));
    when(masker.maskGameForPlayer(game1, PONE)).thenReturn(masked1);
    when(masker.maskGameForPlayer(game2, PONE)).thenReturn(masked2);
    when(masker.maskGameForPlayer(game3, PONE)).thenReturn(masked3);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));

    assertEquals(
        new HashSet<>(Arrays.asList(masked3, masked2, masked1)),
        new HashSet<>(handler.findGames(PONE.getId())));
  }
}
