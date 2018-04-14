package com.jtbdevelopment.games.state.transition

import com.jtbdevelopment.games.scoring.GameScorer
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.SinglePlayerGame
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 4/8/2015
 * Time: 8:22 PM
 *
 * You will most likely need to override the evaluate setup and playing functions
 */
@CompileStatic
abstract class AbstractSPGamePhaseTransitionEngine<IMPL extends SinglePlayerGame> implements GameTransitionEngine<IMPL> {
    @Autowired
    GameScorer<IMPL> gameScorer

    @Override
    public IMPL evaluateGame(final IMPL game) {
        switch (game.gamePhase) {
            case GamePhase.Challenged:
                return evaluateChallengedPhase(game)
            case GamePhase.Setup:
                return evaluateSetupPhase(game)
            case GamePhase.Playing:
                return evaluatePlayingPhase(game)
            case GamePhase.RoundOver:
                return evaluateRoundOverPhase(game)
            case GamePhase.Declined:
                return evaluateDeclinedPhase(game)
            case GamePhase.NextRoundStarted:
                return evaluateNextRoundStartedPhase(game)
            case GamePhase.Quit:
                return evaluateQuitPhase(game)
        }
    }

    //  Likely needs override
    protected IMPL evaluateSetupPhase(final IMPL game) {
        return game
    }

    //  Likely needs override
    protected IMPL evaluatePlayingPhase(final IMPL game) {
        return game
    }

    protected IMPL evaluateRoundOverPhase(final IMPL game) {
        //  TODO
        /*
        if (game.rematchTimestamp != null) {
            return changeStateAndReevaluate(GamePhase.NextRoundStarted, game)
        }
        */
        return (IMPL) gameScorer.scoreGame(game)
    }

    protected IMPL evaluateChallengedPhase(final IMPL game) {
        //  NA
        return game
    }

    protected IMPL evaluateQuitPhase(final IMPL game) {
        return game
    }

    protected IMPL evaluateNextRoundStartedPhase(final IMPL game) {
        return game
    }

    protected IMPL evaluateDeclinedPhase(final IMPL game) {
        return game
    }

    protected IMPL changeStateAndReevaluate(final GamePhase transitionTo, final IMPL game) {
        game.gamePhase = transitionTo
        evaluateGame(game)
    }
}
