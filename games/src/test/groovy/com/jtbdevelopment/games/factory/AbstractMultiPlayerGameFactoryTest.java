package com.jtbdevelopment.games.factory;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import com.jtbdevelopment.games.StringMPGame;
import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
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
public class AbstractMultiPlayerGameFactoryTest {

  private TestAbstractMultiPlayerGameFactory gameFactory;

  @Test
  public void testCreatingNewGame() {
    GameValidator validator = Mockito.mock(GameValidator.class);
    Mockito.when(validator.validateGame(Matchers.isA(Game.class))).thenReturn(true);
    GameInitializer initializer = Mockito.mock(GameInitializer.class);

    gameFactory = new TestAbstractMultiPlayerGameFactory(
        Arrays.asList(initializer, initializer, initializer, initializer),
        Arrays.asList(validator, validator));

    Set<Object> expectedFeatures = new HashSet<>(Arrays.asList("1", 2));
    Player initiatingPlayer = PONE;
    List<Player> players = Arrays.asList(PTWO, PTHREE, PFOUR);
    MultiPlayerGame game = gameFactory.createGame(expectedFeatures, players, initiatingPlayer);

    assertNotNull(game);
    verify(validator, Mockito.times(2)).validateGame(Matchers.any());
    verify(initializer, Mockito.times(4)).initializeGame(Matchers.any());
    assertEquals(expectedFeatures, ((StringMPGame) game).getFeatures());
    assertEquals(new ArrayList<>(Arrays.asList(PTWO, PTHREE, PFOUR, PONE)),
        ((StringMPGame) game).getPlayers());
    assertEquals(initiatingPlayer.getId(), ((StringMPGame) game).getInitiatingPlayer());
    assertEquals(((StringMPGame) game).getCreated(), ((StringMPGame) game).getLastUpdate());
    assertNull(((StringMPGame) game).getCreated());
    assertEquals(GamePhase.Challenged, game.getGamePhase());
    assertNull(game.getVersion());
    assertEquals(1, game.getRound());
    assertNull(((StringMPGame) game).getPreviousId());
  }

  @Test
  public void testCreatingRematchGame() {
    GameValidator validator = Mockito.mock(GameValidator.class);
    Mockito.when(validator.validateGame(Matchers.isA(Game.class))).thenReturn(true);
    GameInitializer initializer = Mockito.mock(GameInitializer.class);

    gameFactory = new TestAbstractMultiPlayerGameFactory(
        Arrays.asList(initializer, initializer, initializer, initializer),
        Arrays.asList(validator, validator));

    Set<Object> expectedFeatures = new HashSet<>(Arrays.asList(32.1, new StringBuilder()));
    Player initiatingPlayer = PONE;
    List<Player<String>> players = Arrays.asList(PTWO, PTHREE, PFOUR, PONE);

    StringMPGame priorGame = new StringMPGame();
    priorGame.setFeatures(expectedFeatures);
    priorGame.setPlayers(players);
    priorGame.setInitiatingPlayer(PTHREE.getId());
    priorGame.setRound(10);
    priorGame.setId("fhjkfhskdfh");
    MultiPlayerGame game = gameFactory.createGame(priorGame, initiatingPlayer);

    assertNotNull(game);
    verify(validator, Mockito.times(2)).validateGame(Matchers.any());
    verify(initializer, Mockito.times(4)).initializeGame(Matchers.any());
    assertEquals(expectedFeatures, ((StringMPGame) game).getFeatures());
    assertEquals(new ArrayList<>(Arrays.asList(PTHREE, PFOUR, PONE, PTWO)),
        ((StringMPGame) game).getPlayers());
    assertEquals(initiatingPlayer.getId(), ((StringMPGame) game).getInitiatingPlayer());
    assertEquals(((StringMPGame) game).getCreated(), ((StringMPGame) game).getLastUpdate());
    assertNull(((StringMPGame) game).getCreated());
    assertEquals(GamePhase.Challenged, game.getGamePhase());
    assertNull(game.getVersion());
    assertEquals((priorGame.getRound() + 1), game.getRound());
    assertEquals(priorGame.getId(), ((StringMPGame) game).getPreviousId());
  }

  @Test
  public void testErrorOnValidationFail() {
    GameValidator validator = Mockito.mock(GameValidator.class);
    Mockito.when(validator.validateGame(Matchers.isA(Game.class))).thenReturn(false);
    Mockito.when(validator.errorMessage()).thenReturn("TADA!");
    gameFactory = new TestAbstractMultiPlayerGameFactory(new ArrayList<>(),
        Arrays.asList(validator, validator));

    Set<Object> expectedFeatures = new HashSet<>(Arrays.asList(54, 55, "56"));
    Player initiatingPlayer = PONE;
    List<Player<String>> players = Arrays.asList(PTWO, PTHREE, PFOUR, PONE);

    StringMPGame priorGame = new StringMPGame();
    priorGame.setFeatures(expectedFeatures);
    priorGame.setPlayers(players);
    priorGame.setInitiatingPlayer(PTHREE.getId());
    try {
      gameFactory.createGame(priorGame, initiatingPlayer);
      Assert.fail("Should have failed");
    } catch (FailedToCreateValidGameException e) {
      verify(validator, Mockito.times(2)).validateGame(Matchers.any());
      assertEquals("System failed to create a valid game.  TADA!  TADA!", e.getMessage());
    }

  }

  private static class TestAbstractMultiPlayerGameFactory extends
      AbstractMultiPlayerGameFactory<StringMPGame, Object> {

    public TestAbstractMultiPlayerGameFactory(
        final List<GameInitializer> gameInitializers,
        final List<GameValidator> gameValidators) {
      super(gameInitializers, gameValidators);
    }

    @Override
    protected StringMPGame newGame() {
      return new StringMPGame();
    }

  }
}
