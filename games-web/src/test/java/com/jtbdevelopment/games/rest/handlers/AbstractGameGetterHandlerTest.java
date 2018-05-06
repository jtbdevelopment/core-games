package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertSame;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException;
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 3/27/15 Time: 6:45 PM
 */
public class AbstractGameGetterHandlerTest {

  private AbstractGameRepository gameRepository = Mockito.mock(AbstractGameRepository.class);
  private AbstractGameGetterHandler handler = new AbstractGameGetterHandler(null, gameRepository) {
  };

  @Test
  public void testValidatePlayerForMultiPlayerGame() {
    StringMPGame mpGame = new StringMPGame();
    mpGame.setPlayers(Arrays.asList(PONE, PTWO));
    handler.validatePlayerForGame(mpGame, PONE);
    handler.validatePlayerForGame(mpGame, PTWO);
  }

  @Test(expected = PlayerNotPartOfGameException.class)
  public void testInvalidValidatePlayerForMultiPlayerGame() {
    StringMPGame mpGame = new StringMPGame();
    mpGame.setPlayers(Arrays.asList(PONE, PTWO));
    handler.validatePlayerForGame(mpGame, PTHREE);
  }

  @Test
  public void testValidatePlayerForSinglePlayerGame() {
    StringSPGame mpGame = new StringSPGame();
    mpGame.setPlayer(PONE);
    handler.validatePlayerForGame(mpGame, PONE);
  }

  @Test(expected = PlayerNotPartOfGameException.class)
  public void testInvalidValidatePlayerForSinglePlayerGame() {
    StringSPGame mpGame = new StringSPGame();
    mpGame.setPlayer(PONE);
    handler.validatePlayerForGame(mpGame, PTHREE);
  }

  @Test
  public void testLoadGame() {
    String id = "X";
    StringMPGame mpGame = GameCoreTestCase.makeSimpleMPGame(id);
    Mockito.when(gameRepository.findById(id)).thenReturn(Optional.of(mpGame));
    assertSame(mpGame, handler.loadGame(id));
  }

  @Test(expected = FailedToFindGameException.class)
  public void testLoadGameFailed() {
    String id = "X";
    Mockito.when(gameRepository.findById(id)).thenReturn(Optional.empty());
    handler.loadGame(id);
  }
}
