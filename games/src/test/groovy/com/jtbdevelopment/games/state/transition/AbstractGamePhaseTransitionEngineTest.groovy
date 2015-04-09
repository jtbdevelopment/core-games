package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState

/**
 * Date: 4/8/2015
 * Time: 8:30 PM
 */
class AbstractGamePhaseTransitionEngineTest extends GameCoreTestCase {
    AbstractGamePhaseTransitionEngine transitionEngine = new AbstractGamePhaseTransitionEngine() {}


    public void testSinglePlayerChallengeTransitionsToSetup() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Setup
    }


    public void testChallengeStayingChallenge() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Pending],
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }

    public void testChallengeToDeclined() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Rejected, (PTWO.id): PlayerState.Pending],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Declined
    }


    public void testChallengeToSetup() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(
                gamePhase: GamePhase.Challenged,
                playerStates: [(PONE.id): PlayerState.Accepted, (PTWO.id): PlayerState.Accepted],
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Setup
    }


    public void testSetup() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(
                gamePhase: GamePhase.Setup,
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }


    public void testPlayingToPlaying() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(
                gamePhase: GamePhase.Playing,
                features: [] as Set,
        )

        assert game.is(transitionEngine.evaluateGame(game))
    }

    /*
    public void testRematchToRematch() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: null)
        assert game.is(transitionEngine.evaluateGame(game))
    }


    public void testRematchToRematched() {
        assert transitionEngine.gameScorer == null

        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.RoundOver, rematchTimestamp: ZonedDateTime.now())
        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.NextRoundStarted
    }
    */


    public void testRematchedToRematched() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.NextRoundStarted)
        assert game.is(transitionEngine.evaluateGame(game))
    }


    public void testDeclinedToDeclined() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.Declined)
        assert game.is(transitionEngine.evaluateGame(game))
    }

    public void testQuitToQuit() {
        assert transitionEngine.gameScorer == null
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.Quit)
        assert game.is(transitionEngine.evaluateGame(game))
    }
}
