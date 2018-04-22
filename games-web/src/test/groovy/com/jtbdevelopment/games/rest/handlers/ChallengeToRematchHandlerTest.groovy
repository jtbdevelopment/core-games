package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.transition.AbstractMPGamePhaseTransitionEngine
import com.jtbdevelopment.games.stringimpl.StringMPGame
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import java.time.Instant

import static com.jtbdevelopment.games.GameCoreTestCase.PONE
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 4/8/2015
 * Time: 10:16 PM
 */
class ChallengeToRematchHandlerTest extends GroovyTestCase {
    ChallengeToRematchHandler handler = new ChallengeToRematchHandler() {}

    void testEligibilityCheck() {
        assert handler.requiresEligibilityCheck(null)
        assert handler.requiresEligibilityCheck('')
        assert handler.requiresEligibilityCheck(1L)
    }

    void testSetsUpRematch() {
        Instant now = Instant.now()
        Thread.sleep(100)
        StringMPGame previous = new StringMPGame(gamePhase: GamePhase.RoundOver, id: 'x')
        StringMPGame previousT = previous.clone()
        StringMPGame previousS = previous.clone()
        StringMPGame previousP = previous.clone()
        StringMPGame newGame = new StringMPGame(previousId: previous.id)
        AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class)
        when(gameFactory.createGame(previousP, PONE)).thenReturn(newGame)
        handler.gameFactory = gameFactory
        def transitionEngine = mock(AbstractMPGamePhaseTransitionEngine.class)
        when(transitionEngine.evaluateGame(previous)).then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                MultiPlayerGame game = invocation.getArguments()[0]
                assert game.rematchTimestamp != null
                assert now < game.rematchTimestamp
                return previousT
            }
        })
        handler.transitionEngine = transitionEngine
        handler.gameRepository = [
                save: {
                    MultiPlayerGame g ->
                        assert g.is(previousT)
                        return previousS
                }
        ] as AbstractMultiPlayerGameRepository
        handler.gamePublisher = [
                publish: {
                    StringMPGame g, Player p ->
                        assert g.is(previousS)
                        assertNull p
                        previousP
                }
        ] as GamePublisher

        newGame.is(handler.handleActionInternal(PONE, previous, null))
    }

    void testNotInRematchPhase() {
        GamePhase.values().find { it != GamePhase.RoundOver }.each {
            StringMPGame previous = new StringMPGame(gamePhase: it, id: "XXXXR")
            try {
                handler.handleActionInternal(PONE, previous, null)
                fail("Should have exceptioned in phase " + it)
            } catch (GameIsNotAvailableToRematchException e) {
                //
            }
        }
    }
}
