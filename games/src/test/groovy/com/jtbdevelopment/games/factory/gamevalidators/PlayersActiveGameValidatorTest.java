package com.jtbdevelopment.games.factory.gamevalidators;

import static com.jtbdevelopment.games.GameCoreTestCase.PINACTIVE1;
import static com.jtbdevelopment.games.GameCoreTestCase.PINACTIVE2;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.games.StringMPGame;
import com.jtbdevelopment.games.StringSPGame;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 4/4/2015 Time: 8:36 PM
 */
public class PlayersActiveGameValidatorTest {

  private AbstractPlayerRepository repository = Mockito.mock(AbstractPlayerRepository.class);
  private PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository);

  @Test
  public void testPassesAllActivesForMPGame() {
    StringMPGame game = new StringMPGame();
    game.setPlayers(Arrays.asList(PONE, PTWO));
    Mockito.when(repository.findAllById(Matchers.eq(Arrays.asList(PONE.getId(), PTWO.getId()))))
        .thenReturn(Arrays.asList(PONE, PTWO));
    assertTrue(validator.validateGame(game));
  }

  @Test
  public void testFailsActiveCountForMPGame() {
    StringMPGame game = new StringMPGame();
    game.setPlayers(Arrays.asList(PONE, PTWO));
    Mockito.when(repository.findAllById(Matchers.eq(Arrays.asList(PONE.getId(), PTWO.getId()))))
        .thenReturn(Arrays.asList(PONE));
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository);
    assertFalse(validator.validateGame(game));
  }

  @Test
  public void testFailsAnInactivePlayerForMPGame() {
    StringMPGame game = new StringMPGame();
    game.setPlayers(Arrays.asList(PONE, PINACTIVE2));
    Mockito
        .when(repository.findAllById(Matchers.eq(Arrays.asList(PONE.getId(), PINACTIVE2.getId()))))
        .thenReturn(Arrays.asList(PONE, PINACTIVE2));
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository);
    assertFalse(validator.validateGame(game));
  }

  @Test
  public void testPassesAllActivesForSPGame() {
    StringSPGame game = new StringSPGame();
    game.setPlayer(PONE);
    Mockito.when(repository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository);
    assertTrue(validator.validateGame(game));
  }

  @Test
  public void testFailsAnInactivePlayerForSPGame() {
    StringSPGame game = new StringSPGame();
    game.setPlayer(PINACTIVE1);
    Mockito.when(repository.findById(PINACTIVE1.getId())).thenReturn(Optional.of(PINACTIVE1));
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository);
    assertFalse(validator.validateGame(game));
  }

  @Test
  public void testFailsAnInactivePlayerViaNotLoadingForSPGame() {
    StringSPGame game = new StringSPGame();
    game.setPlayer(PINACTIVE1);
    Mockito.when(repository.findById(PINACTIVE1.getId())).thenReturn(Optional.empty());
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository);
    assertFalse(validator.validateGame(game));
  }

  @Test
  public void testErrorMessage() {
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator(null);
    assertEquals("Game contains inactive players.", validator.errorMessage());
  }
}
