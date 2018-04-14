package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMPGame
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.PlayerState
import com.jtbdevelopment.games.state.scoring.GameScorer

import java.time.Instant

/**
 * Date: 4/8/2015
 * Time: 8:30 PM
 */
class AbstractMPGamePhaseTransitionEngineTest extends GameCoreTestCase {
    AbstractMPGamePhaseTransitionEngine transitionEngine = new AbstractMPGamePhaseTransitionEngine() {
    }


    public void testSinglePlayerChallengeTransitionsToSetup() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Setup
    }


    public void testChallengeStayingChallenge() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Pending],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Challenged
    }

    public void testChallengeToDeclined() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Rejected, (PTWO.id): PlayerState.Pending],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Declined
    }


    public void testChallengeToSetup() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Accepted],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Setup
    }


    public void testSetup() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Setup,
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }


    public void testPlayingToPlaying() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(
                gamePhase: GamePhase.Playing,
                features: [] as Set,
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }

    public void testRematchToRematch() {
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: null)
        StringMPGame scoredGame = new StringMPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: null)
        transitionEngine.gameScorer = [
                scoreGame: {
                    MultiPlayerGame g ->
                        assert game.is(g)
                        return scoredGame
                }
        ] as GameScorer
        assert scoredGame.is(transitionEngine.evaluateGame(game))
    }


    public void testRematchToRematched() {
        assert transitionEngine.gameScorer == null

        StringMPGame game = new StringMPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: Instant.now())
        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.NextRoundStarted
    }

    public void testRematchedToRematched() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.NextRoundStarted)
        assert game.is(transitionEngine.evaluateGame(game))
    }


    public void testDeclinedToDeclined() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.Declined)
        assert game.is(transitionEngine.evaluateGame(game))
    }

    public void testQuitToQuit() {
        assert transitionEngine.gameScorer == null
        StringMPGame game = new StringMPGame(gamePhase: GamePhase.Quit)
        assert game.is(transitionEngine.evaluateGame(game))
    }
}
