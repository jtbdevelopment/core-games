package com.jtbdevelopment.games.state.transition;

import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 4/8/2015 Time: 8:30 PM
 */
public class AbstractSPGamePhaseTransitionEngineTest {

  private TestSPGamePhaseTransitionEngine transitionEngine = new TestSPGamePhaseTransitionEngine(
      null);

  @Test
  public void testSinglePlayerChallengeDoesNothing() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.Challenged);

    Assert.assertSame(game, transitionEngine.evaluateGame(game));
    Assert.assertEquals(game.getGamePhase(), GamePhase.Challenged);
  }

  @Test
  public void testSetup() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.Setup);

    Assert.assertSame(game, transitionEngine.evaluateGame(game));
    Assert.assertEquals(GamePhase.Setup, game.getGamePhase());
  }

  @Test
  public void testPlayingToPlaying() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.Playing);

    Assert.assertSame(game, transitionEngine.evaluateGame(game));
    Assert.assertEquals(GamePhase.Playing, game.getGamePhase());
  }

  @Test
  public void testRematchToRematch() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.RoundOver);
    game.setId("game");
    StringSPGame scoredGame = new StringSPGame();
    scoredGame.setGamePhase(GamePhase.RoundOver);
    scoredGame.setId("scored");
    GameScorer scorer = Mockito.mock(GameScorer.class);
    Mockito.when(scorer.scoreGame(game)).thenReturn(scoredGame);
    transitionEngine = new TestSPGamePhaseTransitionEngine(scorer);
    Assert.assertSame(scoredGame, transitionEngine.evaluateGame(game));
    Assert.assertEquals(GamePhase.RoundOver, scoredGame.getGamePhase());
  }

  @Test
  public void testRematchedToRematched() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.NextRoundStarted);
    Assert.assertSame(game, transitionEngine.evaluateGame(game));
    Assert.assertEquals(GamePhase.NextRoundStarted, game.getGamePhase());
  }

  @Test
  public void testDeclinedToDeclined() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.Declined);
    Assert.assertSame(game, transitionEngine.evaluateGame(game));
    Assert.assertEquals(GamePhase.Declined, game.getGamePhase());
  }

  @Test
  public void testQuitToQuit() {
    StringSPGame game = new StringSPGame();
    game.setGamePhase(GamePhase.Quit);
    Assert.assertSame(game, transitionEngine.evaluateGame(game));
    Assert.assertEquals(GamePhase.Quit, game.getGamePhase());
  }

  private static class TestSPGamePhaseTransitionEngine extends
      AbstractSPGamePhaseTransitionEngine<String, StringSPGame> {

    public TestSPGamePhaseTransitionEngine(GameScorer gameScorer) {
      super(gameScorer);
    }
  }
}
