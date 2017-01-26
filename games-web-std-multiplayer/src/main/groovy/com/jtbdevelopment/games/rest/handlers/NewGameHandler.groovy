package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
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
    protected AbstractMultiPlayerGameFactory gameFactory
    @Autowired
    protected AbstractMultiPlayerGameRepository gameRepository
    @Autowired(required = false)
    protected GameTransitionEngine transitionEngine
    @Autowired(required = false)
    protected GameMasker gameMasker
    @Autowired(required = false)
    protected GamePublisher gamePublisher
    @Autowired(required = false)
    protected GameEligibilityTracker gameTracker

    Game handleCreateNewGame(
            final Serializable initiatingPlayerID,
            final List<String> playersIDs,
            final Set<?> features) {
        Set<Player> players = loadPlayerMD5s(playersIDs)  //  Load as set to prevent dupes in initial setup
        Player initiatingPlayer = players.find { Player player -> player.id == initiatingPlayerID }
        if (initiatingPlayer == null) {
            initiatingPlayer = loadPlayer(initiatingPlayerID)
        }
        Game game = setupGameWithEligibilityWrapper(initiatingPlayer, features, players)

        if (gamePublisher) {
            gamePublisher.publish(game, initiatingPlayer)
        }
        if (gameMasker) {
            return gameMasker.maskGameForPlayer(game, initiatingPlayer)
        } else {
            return game
        }
    }

    protected Game setupGameWithEligibilityWrapper(
            final Player initiatingPlayer,
            final Set<?> features,
            Set<Player> players) {
        PlayerGameEligibilityResult eligibilityResult = gameTracker?.getGameEligibility(initiatingPlayer)
        if (eligibilityResult?.eligibility == PlayerGameEligibility.NoGamesAvailable) {
            throw new OutOfGamesForTodayException()
        }

        Game game
        try {
            game = setupGame(features, players, initiatingPlayer)
        } catch (Exception e) {
            try {
                if (eligibilityResult) {
                    gameTracker.revertGameEligibility(eligibilityResult)
                }
            } catch (Exception e2) {
                //  TODO - notification
                logger.warn('Failed to revert players game eligibility ' + eligibilityResult, e2)
            }
            throw e;
        }
        game
    }

    protected Game setupGame(
            final Set<?> features,
            final Set<Player> players,
            final Player initiatingPlayer) {
        def game = gameFactory.createGame(features, players.toList(), initiatingPlayer)
        if (transitionEngine) {
            game = transitionEngine.evaluateGame(game)
        }
        gameRepository.save(game)
    }
}
