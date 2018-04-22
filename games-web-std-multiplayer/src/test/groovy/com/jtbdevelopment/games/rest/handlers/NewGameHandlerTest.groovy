package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.stringimpl.StringMPGame
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 11/7/14
 * Time: 9:26 PM
 */
class NewGameHandlerTest extends GameCoreTestCase {
    NewGameHandler handler = new NewGameHandler()

    void testCreateGameAllOptionalPlugins() {
        Set<String> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        StringMPGame game = new StringMPGame()
        game.features.addAll(features)
        StringMPGame savedGame = new StringMPGame()
        savedGame.features = features
        StringMPGame transitionedGame = new StringMPGame()
        StringMPGame publishedGame = new StringMPGame()
        AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class)
        when(gameFactory.createGame(features, players, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory
        handler.gameRepository = [
                save: {
                    assert it.is(transitionedGame)
                    return savedGame
                }
        ] as AbstractMultiPlayerGameRepository
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
                    assert it == PONE.id
                    return Optional.of(PONE)
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
        AbstractMaskedMultiPlayerGame maskedGame = new AbstractMaskedMultiPlayerGame() {}
        handler.gameMasker = [
                maskGameForPlayer: {
                    Game g, Player p ->
                        assert g.is(savedGame)
                        assert p.is(initiatingPlayer)
                        return maskedGame
                }
        ] as GameMasker

        assert maskedGame.is(handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features))
    }

    void testCreateGameNoOptionalPlugins() {
        Set<String> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        StringMPGame game = new StringMPGame()
        game.features.addAll(features)
        StringMPGame savedGame = new StringMPGame()
        savedGame.features = features
        AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class)
        when(gameFactory.createGame(features, players, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory
        handler.gameRepository = [
                save: {
                    assert it.is(game)
                    return savedGame
                }
        ] as AbstractMultiPlayerGameRepository
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
                    assert it == PONE.id
                    return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        assert savedGame.is(handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features))
    }

    void testCreateGameAndTransitionExceptions() {
        Set<Object> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        StringMPGame game = new StringMPGame()
        game.features.addAll(features)
        StringMPGame savedGame = new StringMPGame()
        savedGame.features = features
        boolean revertCalled = false
        AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class)
        when(gameFactory.createGame(features, players, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
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
            handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features)
            fail('exception expected')
        } catch (IllegalArgumentException ignored) {
            assert revertCalled
        }
    }

    void testCreateGameAndRevertExceptionWrapped() {
        Set<Object> features = [1, 3.4, "X"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        StringMPGame game = new StringMPGame()
        game.features.addAll(features)
        StringMPGame savedGame = new StringMPGame()
        savedGame.features = features
        boolean revertCalled = false
        AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class)
        when(gameFactory.createGame(features, players, initiatingPlayer)).thenReturn(game)
        handler.gameFactory = gameFactory
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
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
            handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features)
            fail('exception expected')
        } catch (IllegalArgumentException ignored) {
            assert revertCalled
        } catch (IllegalStateException ignored) {
            fail('should have been wrapped')
        }
    }

    void testCreateGameAndGameCreateExceptions() {
        Set<Object> features = ["1", 5]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        StringMPGame game = new StringMPGame()
        game.features.addAll(features)
        StringMPGame savedGame = new StringMPGame()
        boolean revertCalled = false
        savedGame.features = features
        AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class)
        when(gameFactory.createGame(features, players, initiatingPlayer)).thenThrow(new NumberFormatException())
        handler.gameFactory = gameFactory
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
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
            handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features)
            fail('exception expected')
        } catch (NumberFormatException ignored) {
            assert revertCalled
        }
    }

    void testCreateGameFailsIfNotEligible() {
        Set<Object> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        StringMPGame game = new StringMPGame()
        game.features.addAll(features)
        StringMPGame savedGame = new StringMPGame()
        savedGame.features = features
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
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
            handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features)
            fail('should have failed')
        } catch (OutOfGamesForTodayException ignored) {
            //
        }
    }

    void testInvalidInitiator() {
        Set<Object> features = ["1", 345, new HashMap()]
        List<Player> players = [PONE, PTWO, PTHREE]

        String playerId = "unkw"
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findById   : {
                    assert it == playerId
                    return Optional.empty()
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleCreateNewGame(playerId, players.collect { it.md5 }, features)
            fail("Should have failed")
        } catch (FailedToFindPlayersException ignored) {
            //
        }
    }


    void testNotAllPlayersFound() {
        Set<Object> features = [1.3, "X", 45]
        List<Player> players = [PONE, PTWO, PTHREE]
        Player initiatingPlayer = PFOUR
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return [PONE, PTHREE]
                },
                findById   : {
                    assert it == PFOUR.id
                    return Optional.of(PFOUR)
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features)
            fail("Should have failed")
        } catch (FailedToFindPlayersException ignored) {
            //
        }
    }
}
