package com.jtbdevelopment.games.state.transition;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import java.time.Instant;
import java.util.LinkedHashMap;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 4/8/2015 Time: 8:30 PM
 */
public class AbstractMPGamePhaseTransitionEngineTest {

  private TestMPGamePhaseTransitionEngine transitionEngine = new TestMPGamePhaseTransitionEngine(
      null);

  @Test
  public void testSinglePlayerChallengeTransitionsToSetup() {

    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Challenged);
    LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(2);
    map.put(PONE.getId(), PlayerState.Accepted);
    map.put(PTWO.getId(), PlayerState.Accepted);
    game.setPlayerStates(map);

    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(game.getGamePhase(), GamePhase.Setup);
  }

  @Test
  public void testChallengeStayingChallenge() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Challenged);
    LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(2);
    map.put(PONE.getId(), PlayerState.Accepted);
    map.put(PTWO.getId(), PlayerState.Pending);
    game.setPlayerStates(map);

    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(game.getGamePhase(), GamePhase.Challenged);
  }

  @Test
  public void testChallengeToDeclined() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Challenged);
    LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(3);
    map.put(PONE.getId(), PlayerState.Accepted);
    map.put(PTWO.getId(), PlayerState.Pending);
    map.put(PTHREE.getId(), PlayerState.Rejected);
    game.setPlayerStates(map);

    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(game.getGamePhase(), GamePhase.Declined);
  }

  @Test
  public void testChallengeToSetup() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Challenged);
    LinkedHashMap<String, PlayerState> map = new LinkedHashMap<String, PlayerState>(2);
    map.put(PONE.getId(), PlayerState.Accepted);
    map.put(PTWO.getId(), PlayerState.Accepted);
    game.setPlayerStates(map);

    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(game.getGamePhase(), GamePhase.Setup);
  }

  @Test
  public void testSetup() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Setup);

    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(GamePhase.Setup, game.getGamePhase());
  }

  @Test
  public void testPlayingToPlaying() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Playing);

    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(GamePhase.Playing, game.getGamePhase());
  }

  @Test
  public void testRematchToRematch() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.RoundOver);
    game.setId("game");
    StringMPGame scoredGame = new StringMPGame();
    scoredGame.setGamePhase(GamePhase.RoundOver);
    scoredGame.setId("scored");
    GameScorer scorer = Mockito.mock(GameScorer.class);
    Mockito.when(scorer.scoreGame(game)).thenReturn(scoredGame);
    transitionEngine = new TestMPGamePhaseTransitionEngine(scorer);
    assertSame(scoredGame, transitionEngine.evaluateGame(game));
    assertEquals(GamePhase.RoundOver, scoredGame.getGamePhase());
  }

  @Test
  public void testRematchToRematched() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.RoundOver);
    game.setRematchTimestamp(Instant.now());
    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(game.getGamePhase(), GamePhase.NextRoundStarted);
  }

  @Test
  public void testRematchedToRematched() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.NextRoundStarted);
    GameScorer scorer = Mockito.mock(GameScorer.class);
    transitionEngine = new TestMPGamePhaseTransitionEngine(scorer);
    assertSame(game, transitionEngine.evaluateGame(game));
    Mockito.verify(scorer, Mockito.never()).scoreGame(Matchers.any());
    assertEquals(GamePhase.NextRoundStarted, game.getGamePhase());
  }

  @Test
  public void testDeclinedToDeclined() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Declined);
    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(GamePhase.Declined, game.getGamePhase());
  }

  @Test
  public void testQuitToQuit() {
    StringMPGame game = new StringMPGame();
    game.setGamePhase(GamePhase.Quit);
    assertSame(game, transitionEngine.evaluateGame(game));
    assertEquals(GamePhase.Quit, game.getGamePhase());
  }

  private static class TestMPGamePhaseTransitionEngine extends
      AbstractMPGamePhaseTransitionEngine<String, Object, StringMPGame> {

    public TestMPGamePhaseTransitionEngine(GameScorer gameScorer) {
      super(gameScorer);
    }
  }
}
