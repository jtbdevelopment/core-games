package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringMaskedMPGame;
import com.jtbdevelopment.games.stringimpl.StringMaskedSPGame;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 11/17/14 Time: 6:41 AM
 */
public class GameGetterHandlerTest {

  private final String gameId = "G-id";
  private final StringMPGame mpGame = GameCoreTestCase.makeSimpleMPGame(gameId + "M");
  private final StringMaskedMPGame maskedMPGame = GameCoreTestCase
      .makeSimpleMaskedMPGame(gameId + "MM");
  private final StringSPGame spGame = GameCoreTestCase.makeSimpleSPGame(gameId + "S");
  private final StringMaskedSPGame maskedSPGame = GameCoreTestCase
      .makeSimpleMaskedSPGame(gameId + "SM");
  private GameMasker gameMasker = Mockito.mock(GameMasker.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractGameRepository gameRepository = Mockito.mock(AbstractGameRepository.class);
  private GameGetterHandler handler = new GameGetterHandler(playerRepository, gameRepository,
      gameMasker);

  @Before
  public void setup() {
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    when(gameMasker.maskGameForPlayer(mpGame, PONE)).thenReturn(maskedMPGame);
    when(gameMasker.maskGameForPlayer(spGame, PONE)).thenReturn(maskedSPGame);
    mpGame.setPlayers(Arrays.asList(PTWO, PONE));
    spGame.setPlayer(PONE);
  }

  @Test
  public void testHandlerMultiPlayerWithMasking() {
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(mpGame));
    Assert.assertSame(maskedMPGame, handler.getGame(PONE.getId(), gameId));
  }

  @Test
  public void testHandlerBasicSinglePlayer() {
    when(gameRepository.findById(gameId)).thenReturn(Optional.of(spGame));
    Assert.assertSame(maskedSPGame, handler.getGame(PONE.getId(), gameId));
  }
}
