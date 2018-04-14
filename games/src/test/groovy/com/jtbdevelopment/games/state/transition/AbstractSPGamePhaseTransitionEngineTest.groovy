package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringSPGame
import com.jtbdevelopment.games.scoring.GameScorer
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.SinglePlayerGame

/**
 * Date: 4/8/2015
 * Time: 8:30 PM
 */
class AbstractSPGamePhaseTransitionEngineTest extends GameCoreTestCase {
    AbstractSPGamePhaseTransitionEngine transitionEngine = new AbstractSPGamePhaseTransitionEngine() {
    }


    void testSinglePlayerChallengeDoesNothing() {
        assert transitionEngine.gameScorer == null
        StringSPGame game = new StringSPGame(
                gamePhase: GamePhase.Challenged
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert game.gamePhase == GamePhase.Challenged
    }


    void testSetup() {
        assert transitionEngine.gameScorer == null
        StringSPGame game = new StringSPGame(
                gamePhase: GamePhase.Setup,
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Setup == game.gamePhase
    }


    void testPlayingToPlaying() {
        assert transitionEngine.gameScorer == null
        StringSPGame game = new StringSPGame(
                gamePhase: GamePhase.Playing,
                features: [] as Set,
        )

        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Playing == game.gamePhase
    }

    void testRematchToRematch() {
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.RoundOver)
        StringSPGame scoredGame = new StringSPGame(gamePhase: GamePhase.RoundOver)
        transitionEngine.gameScorer = [
                scoreGame: {
                    SinglePlayerGame g ->
                        assert game.is(g)
                        return scoredGame
                }
        ] as GameScorer
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
        assert transitionEngine.gameScorer == null
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.NextRoundStarted)
        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.NextRoundStarted == game.gamePhase
    }


    void testDeclinedToDeclined() {
        assert transitionEngine.gameScorer == null
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.Declined)
        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Declined == game.gamePhase
    }

    void testQuitToQuit() {
        assert transitionEngine.gameScorer == null
        StringSPGame game = new StringSPGame(gamePhase: GamePhase.Quit)
        assert game.is(transitionEngine.evaluateGame(game))
        assert GamePhase.Quit == game.gamePhase
    }
}
