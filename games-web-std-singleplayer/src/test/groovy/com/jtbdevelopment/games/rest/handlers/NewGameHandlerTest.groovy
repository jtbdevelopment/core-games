package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException
import com.jtbdevelopment.games.factory.AbstractSinglePlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.stringimpl.StringSPGame
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult

import static com.jtbdevelopment.games.GameCoreTestCase.PONE
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 11/7/14
 * Time: 9:26 PM
 */
class NewGameHandlerTest extends GroovyTestCase {
    NewGameHandler handler = new NewGameHandler()

    void testCreateGameAllOptionalPlugins() {
        Set<String> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        Player initiatingPlayer = PONE
        StringSPGame game = new StringSPGame()
        game.features.addAll(features)
        StringSPGame savedGame = new StringSPGame()
        savedGame.features = features
        StringSPGame transitionedGame = new StringSPGame()
        StringSPGame publishedGame = new StringSPGame()
        AbstractSinglePlayerGameFactory gameFactory = mock(AbstractSinglePlayerGameFactory.class)
        when(gameFactory.createGame(features, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory;
        handler.gameRepository = [
                save: {
                    assert it.is(transitionedGame)
                    return savedGame
                }
        ] as AbstractSinglePlayerGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == initiatingPlayer.id
                        return Optional.of(initiatingPlayer)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    assert it.is(game)
                    return transitionedGame
                }
        ] as GameTransitionEngine
        handler.gamePublisher = [
                publish: {
                    Game g, Player p ->
                        assert g.is(savedGame)
                        assert p.is(initiatingPlayer)
                        publishedGame
                }
        ] as GamePublisher
        handler.gameTracker = [
                getGameEligibility: {
                    Player p ->
                        assert p.is(PONE)
                        return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed, player: PONE)
                }
        ] as GameEligibilityTracker
        AbstractMaskedSinglePlayerGame maskedGame = new AbstractMaskedSinglePlayerGame() {}
        handler.gameMasker = [
                maskGameForPlayer: {
                    Game g, Player p ->
                        assert g.is(savedGame)
                        assert p.is(initiatingPlayer)
                        return maskedGame
                }
        ] as GameMasker

        assert maskedGame.is(handler.handleCreateNewGame(initiatingPlayer.id, features))
    }

    void testCreateGameNoOptionalPlugins() {
        Set<String> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        Player initiatingPlayer = PONE
        StringSPGame game = new StringSPGame()
        game.features.addAll(features)
        StringSPGame savedGame = new StringSPGame()
        savedGame.features = features
        AbstractSinglePlayerGameFactory gameFactory = mock(AbstractSinglePlayerGameFactory.class)
        when(gameFactory.createGame(features, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory;
        handler.gameRepository = [
                save: {
                    assert it.is(game)
                    return savedGame
                }
        ] as AbstractSinglePlayerGameRepository
        handler.playerRepository = [
                findById: {
                    assert it == PONE.id
                    return Optional.of(initiatingPlayer)
                }
        ] as AbstractPlayerRepository

        assert savedGame.is(handler.handleCreateNewGame(initiatingPlayer.id, features))
    }

    void testCreateGameAndTransitionExceptions() {
        Set<Object> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        Player initiatingPlayer = PONE
        StringSPGame game = new StringSPGame()
        game.features.addAll(features)
        StringSPGame savedGame = new StringSPGame()
        savedGame.features = features
        boolean revertCalled = false
        AbstractSinglePlayerGameFactory gameFactory = mock(AbstractSinglePlayerGameFactory.class)
        when(gameFactory.createGame(features, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory;
        handler.playerRepository = [
                findById: {
                    assert it == PONE.id
                    return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    assert it.is(game)
                    throw new IllegalArgumentException()
                }
        ] as GameTransitionEngine
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed, player: PONE)
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
                }
        ] as GameEligibilityTracker

        try {
            handler.handleCreateNewGame(initiatingPlayer.id, features)
            fail('exception expected')
        } catch (IllegalArgumentException ignored) {
            assert revertCalled
        }
    }

    void testCreateGameAndRevertExceptionWrapped() {
        Set<Object> features = [1, 3.4, "X"]
        Player initiatingPlayer = PONE
        StringSPGame game = new StringSPGame()
        game.features.addAll(features)
        StringSPGame savedGame = new StringSPGame()
        savedGame.features = features
        boolean revertCalled = false
        AbstractSinglePlayerGameFactory gameFactory = mock(AbstractSinglePlayerGameFactory.class)
        when(gameFactory.createGame(features, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory;
        handler.playerRepository = [
                findById: {
                    assert it == PONE.id
                    return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.transitionEngine = [
                evaluateGame: {
                    assert it.is(game)
                    throw new IllegalArgumentException()
                }
        ] as GameTransitionEngine
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed, player: PONE)
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
                        throw new IllegalStateException()
                }
        ] as GameEligibilityTracker

        try {
            handler.handleCreateNewGame(initiatingPlayer.id, features)
            fail('exception expected')
        } catch (IllegalArgumentException ignored) {
            assert revertCalled
        } catch (IllegalStateException ignored) {
            fail('should have been wrapped')
        }
    }

    void testCreateGameAndGameCreateExceptions() {
        Set<Object> features = ["1", 5]
        Player initiatingPlayer = PONE
        StringSPGame game = new StringSPGame()
        game.features.addAll(features)
        StringSPGame savedGame = new StringSPGame()
        boolean revertCalled = false
        savedGame.features = features
        AbstractSinglePlayerGameFactory gameFactory = mock(AbstractSinglePlayerGameFactory.class)
        when(gameFactory.createGame(features, initiatingPlayer)).thenThrow(new NumberFormatException())
        handler.gameFactory = gameFactory
        handler.playerRepository = [
                findById: {
                    assert it == PONE.id
                    return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        def eligibilityResult = new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed, player: PONE)
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
                }
        ] as GameEligibilityTracker

        try {
            handler.handleCreateNewGame(initiatingPlayer.id, features)
            fail('exception expected')
        } catch (NumberFormatException ignored) {
            assert revertCalled
        }
    }

    void testCreateGameFailsIfNotEligible() {
        Set<Object> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        Player initiatingPlayer = PONE
        StringSPGame game = new StringSPGame()
        game.features.addAll(features)
        StringSPGame savedGame = new StringSPGame()
        savedGame.features = features
        handler.playerRepository = [
                findById: {
                    assert it == PONE.id
                    return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.gameTracker = [
                getGameEligibility: {
                    Player p ->
                        assert p.is(PONE)
                        return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.NoGamesAvailable, player: PONE)
                }
        ] as GameEligibilityTracker

        try {
            handler.handleCreateNewGame(initiatingPlayer.id, features)
            fail('should have failed')
        } catch (OutOfGamesForTodayException ignored) {
            //
        }
    }


    void testInvalidInitiator() {
        Set<Object> features = ["1", 345, new HashMap()]

        String playerId = "unkw"
        handler.playerRepository = [
                findById: {
                    assert it == playerId
                    return Optional.empty()
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleCreateNewGame(playerId, features)
            fail("Should have failed")
        } catch (FailedToFindPlayersException ignored) {
            //
        }
    }


}
