package com.jtbdevelopment.games.factory;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 4/4/2015 Time: 9:41 PM
 */
public class AbstractSinglePlayerGameFactoryTest {

  private TestAbstractSinglePlayerGameFactory gameFactory;

  @Test
  public void testCreatingNewGame() {
    GameValidator validator = Mockito.mock(GameValidator.class);
    Mockito.when(validator.validateGame(Matchers.isA(Game.class))).thenReturn(true);
    GameInitializer initializer = Mockito.mock(GameInitializer.class);

    gameFactory = new TestAbstractSinglePlayerGameFactory(
        Arrays.asList(initializer, initializer, initializer, initializer),
        Arrays.asList(validator, validator));

    Set<Object> expectedFeatures = new HashSet<>(Arrays.asList("1", 2));
    SinglePlayerGame game = gameFactory.createGame(expectedFeatures, PONE);

    assertNotNull(game);
    verify(validator, Mockito.times(2)).validateGame(Matchers.any());
    verify(initializer, Mockito.times(4)).initializeGame(Matchers.any());
    assertEquals(expectedFeatures, ((StringSPGame) game).getFeatures());
    assertEquals(PONE, game.getPlayer());
    assertEquals(((StringSPGame) game).getCreated(), ((StringSPGame) game).getLastUpdate());
    assertNull(((StringSPGame) game).getCreated());
    assertEquals(GamePhase.Setup, game.getGamePhase());
    assertNull(game.getVersion());
    assertEquals(1, game.getRound());
    assertNull(((StringSPGame) game).getPreviousId());
  }

  @Test
  public void testErrorOnValidationFail() {
    GameValidator validator = Mockito.mock(GameValidator.class);
    Mockito.when(validator.validateGame(Matchers.isA(Game.class))).thenReturn(false);
    Mockito.when(validator.errorMessage()).thenReturn("TADA!");
    gameFactory = new TestAbstractSinglePlayerGameFactory(new ArrayList<>(),
        Arrays.asList(validator, validator));

    Set<Object> expectedFeatures = new HashSet<>(Arrays.asList(54, 55, "56"));

    try {
      gameFactory.createGame(expectedFeatures, PONE);
      Assert.fail("Should have failed");
    } catch (FailedToCreateValidGameException e) {
      verify(validator, Mockito.times(2)).validateGame(Matchers.any());
      assertEquals("System failed to create a valid game.  TADA!  TADA!", e.getMessage());
    }

  }

  private static class TestAbstractSinglePlayerGameFactory extends
      AbstractSinglePlayerGameFactory<String, Object, StringSPGame> {

    public TestAbstractSinglePlayerGameFactory(
        final List<GameInitializer> gameInitializers,
        final List<GameValidator> gameValidators) {
      super(gameInitializers, gameValidators);
    }

    @Override
    protected StringSPGame newGame() {
      return new StringSPGame();
    }

  }
}
