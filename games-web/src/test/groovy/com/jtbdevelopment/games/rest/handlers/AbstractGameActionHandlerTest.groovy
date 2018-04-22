package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.stringimpl.StringMPGame
import com.jtbdevelopment.games.stringimpl.StringSPGame
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult

import static com.jtbdevelopment.games.GameCoreTestCase.*

/**
 * Date: 11/10/14
 * Time: 7:06 PM
 */
class AbstractGameActionHandlerTest extends GroovyTestCase {
    private static final String testParam = "TESTPARAM"
    private StringMPGame handledGame = new StringMPGame()
    private final StringMPGame gameParam = new StringMPGame()
    private final String gameId = "238njcn33"

    private class TestHandler extends AbstractGameActionHandler<String, Game> {
        boolean checkEligibility = false
        boolean internalException = false

        @Override
        protected boolean requiresEligibilityCheck(final String param) {
            return checkEligibility
        }

        @Override
        protected Game handleActionInternal(final Player player, final Game game, final String param) {
            assert param == testParam
            assert gameParam.is(game)
            if (internalException) {
                throw new IllegalStateException()
            }
            return handledGame
        }
    }
    private TestHandler handler = new TestHandler()


    void testDefaultRequiresEligibility() {
        assertFalse new AbstractGameActionHandler<String, Game>() {
            protected Game handleActionInternal(final Player player, final Game game, final String param) {
            }
        }.requiresEligibilityCheck(null)
    }

    void testAbstractHandlerBasicWithNoOptionalFeatures() {
        Game saved = new StringMPGame()
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                },
                save    : {
                    Game it ->
                        assert it.is(handledGame)
                        return saved
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        assert saved.is(handler.handleAction(PONE.id, gameId, testParam))
    }

    void testAbstractHandlerBasicWithAllFeaturesSinglePlayer() {
        Game saved = new StringSPGame()
        Game transitioned = new StringSPGame()
        Game published = new StringSPGame()
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                },
                save    : {
                    Game it ->
                        assert it.is(transitioned)
                        return saved
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    Game it ->
                        assert it.is(handledGame)
                        return transitioned
                }
        ] as GameTransitionEngine
        handler.gamePublisher = [
                publish: {
                    Game g, Player p ->
                        assert g.is(saved)
                        assert p.is(PONE)
                        published
                }
        ] as GamePublisher

        assert published.is(handler.handleAction(PONE.id, gameId, testParam))
    }

    void testAbstractHandlerBasicWithAllFeatures() {
        Game saved = new StringMPGame()
        Game transitioned = new StringMPGame()
        Game published = new StringMPGame()
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                },
                save    : {
                    Game it ->
                        assert it.is(transitioned)
                        return saved
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    Game it ->
                        assert it.is(handledGame)
                        return transitioned
                }
        ] as GameTransitionEngine
        handler.gamePublisher = [
                publish: {
                    Game g, Player p ->
                        assert g.is(saved)
                        assert p.is(PONE)
                        published
                }
        ] as GamePublisher
        AbstractMaskedMultiPlayerGame maskedMultiPlayerGame = new AbstractMaskedMultiPlayerGame() {}
        handler.gameMasker = [
                maskGameForPlayer: {
                    Game g, Player p ->
                        assert g.is(published)
                        assert p.is(PONE)
                        return maskedMultiPlayerGame
                }
        ] as GameMasker

        assert maskedMultiPlayerGame.is(handler.handleAction(PONE.id, gameId, testParam))
    }

    void testAbstractHandlerWithEligibilityCheckAndEligible() {
        handler.checkEligibility = true
        Game saved = new StringMPGame()
        Game transitioned = new StringMPGame()
        Game published = new StringMPGame()
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                },
                save    : {
                    Game it ->
                        assert it.is(transitioned)
                        return saved
                }
        ] as AbstractGameRepository
        handler.gameTracker = [
                getGameEligibility: {
                    Player p ->
                        assert p.is(PONE)
                        return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
                }
        ] as GameEligibilityTracker
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    Game it ->
                        assert it.is(handledGame)
                        return transitioned
                }
        ] as GameTransitionEngine
        handler.gamePublisher = [
                publish: {
                    Game g, Player p ->
                        assert g.is(saved)
                        assert p.is(PONE)
                        published
                }
        ] as GamePublisher
        AbstractMaskedMultiPlayerGame maskedMultiPlayerGame = new AbstractMaskedMultiPlayerGame() {}
        handler.gameMasker = [
                maskGameForPlayer: {
                    Game g, Player p ->
                        assert g.is(published)
                        assert p.is(PONE)
                        return maskedMultiPlayerGame
                }
        ] as GameMasker

        assert maskedMultiPlayerGame.is(handler.handleAction(PONE.id, gameId, testParam))
    }

    void testAbstractHandlerWithEligibilityCheckAndNotEligible() {
        handler.checkEligibility = true
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                }
        ] as AbstractGameRepository
        handler.gameTracker = [
                getGameEligibility: {
                    Player p ->
                        assert p.is(PONE)
                        return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.NoGamesAvailable)
                }
        ] as GameEligibilityTracker
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail('should have exceptioned')
        } catch (OutOfGamesForTodayException ignored) {
            //
        }
    }

    void testAbstractHandlerWithEligibilityCheckAndHandleInternalExceptions() {
        handler.checkEligibility = true
        handler.internalException = true
        boolean revertCalled = false
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                }
        ] as AbstractGameRepository
        handler.gameTracker = [
                getGameEligibility   : {
                    Player p ->
                        assert p.is(PONE)
                        return eligibilityResult
                },
                revertGameEligibility: {
                    PlayerGameEligibilityResult r ->
                        assert r.is(eligibilityResult)
                        revertCalled = true
                        return
                }
        ] as GameEligibilityTracker
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail('should have exception')
        } catch (IllegalStateException ignored) {
            assert revertCalled
        }
    }

    void testAbstractHandlerWithEligibilityCheckAndTransitionExceptions() {
        handler.checkEligibility = true
        gameParam.players = [PONE, PTWO]
        boolean revertCalled = false
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                }
        ] as AbstractGameRepository
        handler.gameTracker = [
                getGameEligibility   : {
                    Player p ->
                        assert p.is(PONE)
                        return eligibilityResult
                },
                revertGameEligibility: {
                    PlayerGameEligibilityResult r ->
                        assert r.is(eligibilityResult)
                        revertCalled = true
                        return
                }
        ] as GameEligibilityTracker
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    Game it ->
                        assert it.is(handledGame)
                        throw new IllegalArgumentException()
                }
        ] as GameTransitionEngine

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail('should have exceptioned')
        } catch (IllegalArgumentException ignored) {
            assert revertCalled
        }
    }

    void testAbstractHandlerWithEligibilityCheckAndRevertExceptionsAlso() {
        handler.checkEligibility = true
        gameParam.players = [PONE, PTWO]
        boolean revertCalled = false
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                }
        ] as AbstractGameRepository
        handler.gameTracker = [
                getGameEligibility   : {
                    Player p ->
                        assert p.is(PONE)
                        return eligibilityResult
                },
                revertGameEligibility: {
                    PlayerGameEligibilityResult r ->
                        assert r.is(eligibilityResult)
                        revertCalled = true
                        throw new IllegalAccessException()
                }
        ] as GameEligibilityTracker
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    Game it ->
                        assert it.is(handledGame)
                        throw new IllegalArgumentException()
                }
        ] as GameTransitionEngine

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail('should have exceptioned')
        } catch (IllegalArgumentException ignored) {
            assert revertCalled
        } catch (IllegalAccessException ignored) {
            fail('Should have caught and discarded IllegalAccessException')
        }
    }

    void testAbstractHandlerCantLoadGame() {
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.empty()
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail("should have failed")
        } catch (FailedToFindGameException ignored) {

        }
    }


    void testAbstractHandlerInvalidPlayer() {
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(gameParam)
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PTHREE.id
                        return Optional.of(PTHREE)
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PTHREE.id, gameId, testParam)
            fail("should have failed")
        } catch (PlayerNotPartOfGameException ignored) {
            //
        }
    }
}
