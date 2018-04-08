package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.transition.AbstractMPGamePhaseTransitionEngine

import java.time.Instant

/**
 * Date: 4/8/2015
 * Time: 10:16 PM
 */
class ChallengeToRematchHandlerTest extends GameCoreTestCase {
    ChallengeToRematchHandler handler = new ChallengeToRematchHandler() {}

    void testEligibilityCheck() {
        assert handler.requiresEligibilityCheck(null)
        assert handler.requiresEligibilityCheck('')
        assert handler.requiresEligibilityCheck(1L)
    }

    void testSetsUpRematch() {
        Instant now = Instant.now()
        Thread.sleep(100)
        GameCoreTestCase.StringMPGame previous = new GameCoreTestCase.StringMPGame(gamePhase: GamePhase.RoundOver, id: 'x')
        GameCoreTestCase.StringMPGame previousT = previous.clone()
        GameCoreTestCase.StringMPGame previousS = previous.clone()
        GameCoreTestCase.StringMPGame previousP = previous.clone()
        GameCoreTestCase.StringMPGame newGame = new GameCoreTestCase.StringMPGame(previousId: previous.id)
        handler.gameFactory = [
                createGame: {
                    GameCoreTestCase.StringMPGame g, Player p ->
                        assert g.is(previousP)
                        assert p.is(PONE)
                        newGame
                }
        ] as AbstractMultiPlayerGameFactory
        handler.transitionEngine = [
                evaluateGame: {
                    MultiPlayerGame g ->
                        assert g.is(previous)
                        assert g.rematchTimestamp != null
                        assert now < g.rematchTimestamp
                        return previousT
                }
        ] as AbstractMPGamePhaseTransitionEngine
        handler.gameRepository = [
                save: {
                    MultiPlayerGame g ->
                        assert g.is(previousT)
                        return previousS
                }
        ] as AbstractMultiPlayerGameRepository
        handler.gamePublisher = [
                publish: {
                    GameCoreTestCase.StringMPGame g, Player p ->
                        assert g.is(previousS)
                        assertNull p
                        previousP
                }
        ] as GamePublisher

        newGame.is(handler.handleActionInternal(PONE, previous, null))
    }

    void testNotInRematchPhase() {
        GamePhase.values().find { it != GamePhase.RoundOver }.each {
            GameCoreTestCase.StringMPGame previous = new GameCoreTestCase.StringMPGame(gamePhase: it, id: "XXXXR")
            try {
                handler.handleActionInternal(PONE, previous, null)
                fail("Should have exceptioned in phase " + it)
            } catch (GameIsNotAvailableToRematchException e) {
                //
            }
        }
    }
}
