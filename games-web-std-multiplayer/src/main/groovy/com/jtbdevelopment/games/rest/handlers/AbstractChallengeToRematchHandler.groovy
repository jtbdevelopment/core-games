package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.exceptions.input.GameIsNotAvailableToRematchException
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 11/4/2014
 * Time: 9:11 PM
 */
@CompileStatic
abstract class AbstractChallengeToRematchHandler extends AbstractGameActionHandler<Object, MultiPlayerGame> {
    public static final ZoneId GMT = ZoneId.of("GMT")
    @Autowired
    protected AbstractMultiPlayerGameFactory gameFactory

    @Override
    protected boolean requiresEligibilityCheck(final Object param) {
        return true;
    }

    @Override
    protected MultiPlayerGame handleActionInternal(
            final Player player, final MultiPlayerGame previousGame, final Object param) {
        if (previousGame.gamePhase != GamePhase.RoundOver) {
            throw new GameIsNotAvailableToRematchException()
        }
        previousGame.rematchTimestamp = ZonedDateTime.now(GMT)
        MultiPlayerGame transitioned = (MultiPlayerGame) gamePublisher.publish(
                (MultiPlayerGame) gameRepository.save(
                        transitionEngine.evaluateGame(previousGame)),
                null)
        //  We set to system player so it gets published to all players, including this one
        //  TODO - handle newGame setup failing..
        MultiPlayerGame newGame = setupGame(transitioned, player)
        newGame
    }

    protected MultiPlayerGame setupGame(final MultiPlayerGame previousGame, final Player initiatingPlayer) {
        gameFactory.createGame(previousGame, initiatingPlayer)
    }
}
