package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
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
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult

/**
 * Date: 11/10/14
 * Time: 7:06 PM
 */
class AbstractGameActionHandlerTest extends GameCoreTestCase {
    private static final String testParam = "TESTPARAM"
    private GameCoreTestCase.StringMPGame handledGame = new GameCoreTestCase.StringMPGame()
    private final GameCoreTestCase.StringMPGame gameParam = new GameCoreTestCase.StringMPGame()
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

    public void testAbstractHandlerBasicWithNoOptionalFeatures() {
        Game saved = new GameCoreTestCase.StringMPGame();
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
                },
                save   : {
                    Game it ->
                        assert it.is(handledGame)
                        return saved
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
                }
        ] as AbstractPlayerRepository

        assert saved.is(handler.handleAction(PONE.id, gameId, testParam))
    }

    public void testAbstractHandlerBasicWithAllFeaturesSinglePlayer() {
        Game saved = new GameCoreTestCase.StringSPGame();
        Game transitioned = new GameCoreTestCase.StringSPGame();
        Game published = new GameCoreTestCase.StringSPGame();
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
                },
                save   : {
                    Game it ->
                        assert it.is(transitioned)
                        return saved
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
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

    public void testAbstractHandlerBasicWithAllFeatures() {
        Game saved = new GameCoreTestCase.StringMPGame();
        Game transitioned = new GameCoreTestCase.StringMPGame();
        Game published = new GameCoreTestCase.StringMPGame();
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
                },
                save   : {
                    Game it ->
                        assert it.is(transitioned)
                        return saved
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
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

    public void testAbstractHandlerWithEligibilityCheckAndEligible() {
        handler.checkEligibility = true
        Game saved = new GameCoreTestCase.StringMPGame();
        Game transitioned = new GameCoreTestCase.StringMPGame();
        Game published = new GameCoreTestCase.StringMPGame();
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
                },
                save   : {
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
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
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

    public void testAbstractHandlerWithEligibilityCheckAndNotEligible() {
        handler.checkEligibility = true
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
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
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail('should have exceptioned')
        } catch (OutOfGamesForTodayException e) {
            //
        }
    }

    public void testAbstractHandlerWithEligibilityCheckAndHandleInternalExceptions() {
        handler.checkEligibility = true
        handler.internalException = true
        boolean revertCalled = false
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
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
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail('should have exception')
        } catch (IllegalStateException e) {
            assert revertCalled
        }
    }

    public void testAbstractHandlerWithEligibilityCheckAndTransitionExceptions() {
        handler.checkEligibility = true
        gameParam.players = [PONE, PTWO]
        boolean revertCalled = false
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
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
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
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
        } catch (IllegalArgumentException e) {
            assert revertCalled
        }
    }

    public void testAbstractHandlerWithEligibilityCheckAndRevertExceptionsAlso() {
        handler.checkEligibility = true
        gameParam.players = [PONE, PTWO]
        boolean revertCalled = false
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed)
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
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
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
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
        } catch (IllegalArgumentException e) {
            assert revertCalled
        } catch (IllegalAccessException e) {
            fail('Should have caught and discarded IllegalAccessException')
        }
    }

    public void testAbstractHandlerCantLoadGame() {
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return null
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PONE.id
                        return PONE
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PONE.id, gameId, testParam)
            fail("should have failed")
        } catch (FailedToFindGameException e) {

        }
    }


    public void testAbstractHandlerInvalidPlayer() {
        gameParam.players = [PONE, PTWO]
        handler.gameRepository = [
                findOne: {
                    String it ->
                        assert it == gameId
                        return gameParam
                }
        ] as AbstractGameRepository
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PTHREE.id
                        return PTHREE
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleAction(PTHREE.id, gameId, testParam)
            fail("should have failed")
        } catch (PlayerNotPartOfGameException e) {
            //
        }
    }
}
