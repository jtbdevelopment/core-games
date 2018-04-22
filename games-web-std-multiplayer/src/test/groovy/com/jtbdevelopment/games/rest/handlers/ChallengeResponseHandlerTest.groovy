package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.exceptions.input.TooLateToRespondToChallengeException
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import com.jtbdevelopment.games.stringimpl.StringMPGame

/**
 * Date: 11/9/2014
 * Time: 8:21 PM
 */
class ChallengeResponseHandlerTest extends GameCoreTestCase {
    ChallengeResponseHandler handler = new ChallengeResponseHandler()

    public void testRequiresEligibilityForAcceptButNotOtherStates() {
        assert handler.requiresEligibilityCheck(PlayerState.Accepted)
        PlayerState.values().findAll { it != PlayerState.Accepted }.each {
            assertFalse handler.requiresEligibilityCheck(it)
        }
    }

    public void testExceptionOnBadPhases() {
        GamePhase.values().findAll { it != GamePhase.Declined && it != GamePhase.Challenged }.each {
            StringMPGame game = new StringMPGame(gamePhase: it)
            try {
                handler.handleActionInternal(PONE, game, PlayerState.Rejected)
                fail("Should have exceptioned on state " + it)
            } catch (TooLateToRespondToChallengeException e) {
                //
            }
        }
    }


    public void testSetsStateForPlayer() {
        [GamePhase.Declined, GamePhase.Challenged].each {
            GamePhase gamePhase ->
                PlayerState.findAll { it != PlayerState.Pending }.each {
                    PlayerState response ->
                        StringMPGame game = new StringMPGame(
                                gamePhase: gamePhase,
                                playerStates: [(PONE.id)  : PlayerState.Pending,
                                               (PTWO.id)  : PlayerState.Rejected,
                                               (PTHREE.id): PlayerState.Pending,
                                               (PFOUR.id) : PlayerState.Accepted,
                                ])
                        handler.handleActionInternal(PONE, game, response)
                        assert game.playerStates[PONE.id] == response
                        assert game.playerStates[PTWO.id] == PlayerState.Rejected
                        assert game.playerStates[PTHREE.id] == PlayerState.Pending
                        assert game.playerStates[PFOUR.id] == PlayerState.Accepted
                }
        }
    }


    public void testOverridesResponseForPlayer() {
        [GamePhase.Declined, GamePhase.Challenged].each {
            GamePhase gamePhase ->
                PlayerState.findAll { it != PlayerState.Pending }.each {
                    PlayerState response ->
                        StringMPGame game = new StringMPGame(
                                gamePhase: gamePhase,
                                playerStates: [(PONE.id)  : PlayerState.Accepted,
                                               (PTWO.id)  : PlayerState.Rejected,
                                               (PTHREE.id): PlayerState.Pending,
                                               (PFOUR.id) : PlayerState.Accepted,
                                ])
                        handler.handleActionInternal(PONE, game, response)
                        assert game.playerStates[PONE.id] == response
                        assert game.playerStates[PTWO.id] == PlayerState.Rejected
                        assert game.playerStates[PTHREE.id] == PlayerState.Pending
                        assert game.playerStates[PFOUR.id] == PlayerState.Accepted
                }
        }
    }
}
