package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMPGame
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import com.jtbdevelopment.games.state.scoring.GameScorer

import java.time.Instant

import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

/**
 * Date: 4/8/2015
 * Time: 8:30 PM
 */
class AbstractMPGamePhaseTransitionEngineTest extends GameCoreTestCase {
    private
    static class TestMPGamePhaseTransitionEngine extends AbstractMPGamePhaseTransitionEngine {
        TestMPGamePhaseTransitionEngine(GameScorer gameScorer) {
            super(gameScorer)
        }
    }
    TestMPGamePhaseTransitionEngine transitionEngine = new TestMPGamePhaseTransitionEngine(null)


    void testSinglePlayerChallengeTransitionsToSetup() {
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Setup
    }


    void testChallengeStayingChallenge() {
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Pending],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Challenged
    }

    void testChallengeToDeclined() {
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Rejected, (PTWO.id): PlayerState.Pending],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Declined
    }


    void testChallengeToSetup() {
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Accepted],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Setup
    }


    void testSetup() {
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Setup,
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }


    void testPlayingToPlaying() {
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Playing,
                features: [] as Set,
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }

    void testRematchToRematch() {
        StringMPGame game = new StringMPGame(id: '1', gamePhase: GamePhase.RoundOver, rematchTimestamp: null)
        StringMPGame scoredGame = new StringMPGame(id: '1', gamePhase: GamePhase.RoundOver, rematchTimestamp: null)
        GameScorer scorer = mock(GameScorer.class)
        when(scorer.scoreGame(game)).thenReturn(scoredGame)
        transitionEngine = new TestMPGamePhaseTransitionEngine(scorer)
        assert scoredGame.is(transitionEngine.evaluateGame(game))
    }


    void testRematchToRematched() {

        StringMPGame game = new StringMPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: Instant.now())
        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.NextRoundStarted
    }

    void testRematchedToRematched() {
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.NextRoundStarted)
        GameScorer scorer = mock(GameScorer.class)
        transitionEngine = new TestMPGamePhaseTransitionEngine(scorer)
        assert game.is(transitionEngine.evaluateGame(game))
        verify(scorer, never()).scoreGame(any())
    }


    void testDeclinedToDeclined() {
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.Declined)
        assert game.is(transitionEngine.evaluateGame(game))
    }

    void testQuitToQuit() {
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.Quit)
        assert game.is(transitionEngine.evaluateGame(game))
    }
}
