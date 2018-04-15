package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringSPGame
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.scoring.GameScorer

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


    void testSinglePlayerChallengeDoesNothing() {
        StringSPGame game = new StringSPGame(
                gamePhase: GamePhase.Challenged
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Challenged
    }


    void testSetup() {
        StringSPGame game = new StringSPGame(
                gamePhase: GamePhase.Setup,
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Setup == game.gamePhase
    }


    void testPlayingToPlaying() {
        StringSPGame game = new StringSPGame(
                gamePhase: GamePhase.Playing,
                features: [] as Set,
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Playing == game.gamePhase
    }

    void testRematchToRematch() {
        StringSPGame game = new StringSPGame(id: '1', gamePhase: GamePhase.RoundOver)
        StringSPGame scoredGame = new StringSPGame(id: '1', gamePhase: GamePhase.RoundOver)
        GameScorer scorer = mock(GameScorer.class)
        when(scorer.scoreGame(game)).thenReturn(scoredGame)
        transitionEngine = new TestSPGamePhaseTransitionEngine(scorer)
        assert scoredGame.is(transitionEngine.evaluateGame(game))
        assert GamePhase.RoundOver == scoredGame.gamePhase
    }

    /*  TODO
    void testRematchToRematched() {
        assert transitionEngine.gameScorer == null

        StringSPGame game = new StringSPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: ZonedDateTime.now())
        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.NextRoundStarted
    }
    */

    void testRematchedToRematched() {
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.NextRoundStarted)
        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.NextRoundStarted == game.gamePhase
    }


    void testDeclinedToDeclined() {
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.Declined)
        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Declined == game.gamePhase
    }

    void testQuitToQuit() {
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.Quit)
        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Quit == game.gamePhase
    }
}
