package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringSPGame
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.scoring.GameScorer
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 4/8/2015
 * Time: 8:30 PM
 */
class AbstractSPGamePhaseTransitionEngineTest extends GameCoreTestCase {
    private
    static class TestSPGamePhaseTransitionEngine extends AbstractSPGamePhaseTransitionEngine {
        TestSPGamePhaseTransitionEngine(GameScorer gameScorer) {
            super(gameScorer)
        }
    }
    TestSPGamePhaseTransitionEngine transitionEngine = new TestSPGamePhaseTransitionEngine(null)

    @Test
    void testSinglePlayerChallengeDoesNothing() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.Challenged)

        assertSame(game, transitionEngine.evaluateGame(game))
        assertEquals game.gamePhase, GamePhase.Challenged
    }


    @Test
    void testSetup() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.Setup)

        assertSame(game, transitionEngine.evaluateGame(game))
        assertEquals GamePhase.Setup, game.gamePhase
    }

    @Test
    void testPlayingToPlaying() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.Playing)

        assertSame(game, transitionEngine.evaluateGame(game))
        assertEquals GamePhase.Playing, game.gamePhase
    }

    @Test
    void testRematchToRematch() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.RoundOver)
        game.setId("game")
        StringSPGame scoredGame = new StringSPGame(id: '1', gamePhase: GamePhase.RoundOver)
        scoredGame.setGamePhase(GamePhase.RoundOver)
        scoredGame.setId("scored")
        GameScorer scorer = mock(GameScorer.class)
        when(scorer.scoreGame(game)).thenReturn(scoredGame)
        transitionEngine = new TestSPGamePhaseTransitionEngine(scorer)
        assertSame(scoredGame, transitionEngine.evaluateGame(game))
        assertEquals GamePhase.RoundOver, scoredGame.gamePhase
    }

    @Test
    void testRematchedToRematched() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.NextRoundStarted)
        assertSame(game, transitionEngine.evaluateGame(game))
        assertEquals GamePhase.NextRoundStarted, game.gamePhase
    }


    @Test
    void testDeclinedToDeclined() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.Declined)
        assertSame(game, transitionEngine.evaluateGame(game))
        assertEquals GamePhase.Declined, game.gamePhase
    }

    @Test
    void testQuitToQuit() {
        StringSPGame game = new StringSPGame()
        game.setGamePhase(GamePhase.Quit)
        assertSame(game, transitionEngine.evaluateGame(game))
        assertEquals GamePhase.Quit, game.gamePhase
    }
}
