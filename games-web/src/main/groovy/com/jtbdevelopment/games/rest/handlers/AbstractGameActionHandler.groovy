package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException
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

/**
 * Date: 11/9/2014
 * Time: 8:36 PM
 */
@CompileStatic
abstract class AbstractGameActionHandler<T, IMPL extends Game> extends AbstractGameGetterHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGameActionHandler.class)

    @Autowired
    protected GameTransitionEngine transitionEngine
    @Autowired(required = false)
    protected GamePublisher gamePublisher
    @Autowired(required = false)
    protected GameEligibilityTracker gameTracker
    @Autowired(required = false)
    protected GameMasker gameMasker

    abstract protected IMPL handleActionInternal(final Player player, final IMPL game, final T param)

    Game handleAction(final Serializable playerID, final Serializable gameID, final T param = null) {
        Player player = loadPlayer(playerID)
        IMPL game = (IMPL) loadGame(gameID)
        validatePlayerForGame(game, player)
        Game updatedGame = updateGameWithEligibilityWrapper(player, game, param)

        if (gamePublisher) {
            updatedGame = gamePublisher.publish(updatedGame, player)
        }
        if (gameMasker) {
            return gameMasker.maskGameForPlayer(updatedGame, player)
        } else {
            return updatedGame
        }
    }

    protected Game updateGameWithEligibilityWrapper(final Player player, final IMPL game, final T param) {
        Game updatedGame
        PlayerGameEligibilityResult eligibilityResult = null
        if (gameTracker && requiresEligibilityCheck(param)) {
            eligibilityResult = (PlayerGameEligibilityResult) gameTracker.getGameEligibility(player)
            if (eligibilityResult.eligibility == PlayerGameEligibility.NoGamesAvailable) {
                throw new OutOfGamesForTodayException()
            }
        }
        try {
            updatedGame = updateGame(player, game, param)
        } catch (Exception e) {
            try {
                if (eligibilityResult) {
                    gameTracker.revertGameEligibility(eligibilityResult)
                }
            } catch (Exception e2) {
                //  TODO - notify
                logger.warn('Failed to revert players game eligibility ' + eligibilityResult, e2)
            }
            throw e
        }
        updatedGame
    }

    protected Game updateGame(final Player player, final IMPL game, final T param) {
        def updated = rotateTurnBasedGame(handleActionInternal(player, game, param))
        if (transitionEngine) {
            updated = transitionEngine.evaluateGame(updated)
        }
        return gameRepository.save(updated)
    }

    //  No rotation
    @SuppressWarnings("GrMethodMayBeStatic")
    protected IMPL rotateTurnBasedGame(final IMPL game) {
        return game
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected boolean requiresEligibilityCheck(final T param) {
        return false
    }

}
