package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException
import com.jtbdevelopment.games.factory.AbstractSinglePlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 11/4/2014
 * Time: 9:10 PM
 */
@CompileStatic
@Component
class NewGameHandler extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(NewGameHandler.class)

    @Autowired
    protected AbstractSinglePlayerGameFactory gameFactory
    @Autowired
    protected AbstractSinglePlayerGameRepository gameRepository
    @Autowired(required = false)
    protected GameTransitionEngine transitionEngine
    @Autowired(required = false)
    protected GameMasker gameMasker
    @Autowired(required = false)
    protected GamePublisher gamePublisher
    @Autowired(required = false)
    protected GameEligibilityTracker gameTracker

    Game handleCreateNewGame(
            final Serializable playerID,
            final Set<?> features) {
        Player player = loadPlayer(playerID)  //  Load as set to prevent dupes in initial setup
        Game game = setupGameWithEligibilityWrapper(features, player)

        if (gamePublisher) {
            gamePublisher.publish(game, player)
        }
        if (gameMasker) {
            return gameMasker.maskGameForPlayer(game, player)
        } else {
            return game
        }
    }

    protected Game setupGameWithEligibilityWrapper(
            final Set<?> features,
            Player player) {
        PlayerGameEligibilityResult eligibilityResult = gameTracker?.getGameEligibility(player)
        if (eligibilityResult?.eligibility == PlayerGameEligibility.NoGamesAvailable) {
            throw new OutOfGamesForTodayException()
        }

        Game game
        try {
            game = setupGame(features, player)
        } catch (Exception e) {
            try {
                if (eligibilityResult) {
                    gameTracker.revertGameEligibility(eligibilityResult)
                }
            } catch (Exception e2) {
                //  TODO - notification
                logger.warn('Failed to revert players game eligibility ' + eligibilityResult, e2)
            }
            throw e
        }
        game
    }

    protected Game setupGame(
            final Set<?> features,
            final Player player) {
        def game = gameFactory.createGame(features, player)
        if (transitionEngine) {
            game = transitionEngine.evaluateGame(game)
        }
        gameRepository.save(game)
    }
}
