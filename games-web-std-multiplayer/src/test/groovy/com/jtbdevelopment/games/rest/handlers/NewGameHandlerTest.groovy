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
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult

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
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
        game.features.addAll(features)
        GameCoreTestCase.StringMPGame savedGame = new GameCoreTestCase.StringMPGame()
        savedGame.features = features
        GameCoreTestCase.StringMPGame transitionedGame = new GameCoreTestCase.StringMPGame()
        GameCoreTestCase.StringMPGame publishedGame = new GameCoreTestCase.StringMPGame()
        handler.gameFactory = [createGame: { a, b, c ->
            assert a == features
            assert b == players
            assert c == initiatingPlayer
            game
        }] as AbstractMultiPlayerGameFactory
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
                findOne    : {
                    assert it == PONE.id
                    return PONE
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

    public void testCreateGameNoOptionalPlugins() {
        Set<String> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
        game.features.addAll(features)
        GameCoreTestCase.StringMPGame savedGame = new GameCoreTestCase.StringMPGame()
        savedGame.features = features
        handler.gameFactory = [createGame: { a, b, c ->
            assert a == features
            assert b == players
            assert c == initiatingPlayer
            game
        }] as AbstractMultiPlayerGameFactory
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
                findOne    : {
                    assert it == PONE.id
                    return PONE
                }
        ] as AbstractPlayerRepository

        assert savedGame.is(handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features))
    }

    public void testCreateGameAndTransitionExceptions() {
        Set<Object> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
        game.features.addAll(features)
        GameCoreTestCase.StringMPGame savedGame = new GameCoreTestCase.StringMPGame()
        savedGame.features = features
        boolean revertCalled = false
        handler.gameFactory = [createGame: { a, b, c ->
            assert a == features
            assert b == players
            assert c == initiatingPlayer
            game
        }] as AbstractMultiPlayerGameFactory
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findOne    : {
                    assert it == PONE.id
                    return PONE
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
        } catch (IllegalArgumentException e) {
            assert revertCalled
        }
    }

    void testCreateGameAndRevertExceptionWrapped() {
        Set<Object> features = [1, 3.4, "X"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
        game.features.addAll(features)
        GameCoreTestCase.StringMPGame savedGame = new GameCoreTestCase.StringMPGame()
        savedGame.features = features
        boolean revertCalled = false
        handler.gameFactory = [createGame: { a, b, c ->
            assert a == features
            assert b == players
            assert c == initiatingPlayer
            game
        }] as AbstractMultiPlayerGameFactory
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findOne    : {
                    assert it == PONE.id
                    return PONE
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
        } catch (IllegalArgumentException e) {
            assert revertCalled
        } catch (IllegalStateException e) {
            fail('should have been wrapped')
        }
    }

    public void testCreateGameAndGameCreateExceptions() {
        Set<Object> features = ["1", 5]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
        game.features.addAll(features)
        GameCoreTestCase.StringMPGame savedGame = new GameCoreTestCase.StringMPGame()
        boolean revertCalled = false
        savedGame.features = features
        handler.gameFactory = [createGame: { a, b, c ->
            assert a == features
            assert b == players
            assert c == initiatingPlayer
            throw new NumberFormatException()
        }] as AbstractMultiPlayerGameFactory
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findOne    : {
                    assert it == PONE.id
                    return PONE
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
        } catch (NumberFormatException e) {
            assert revertCalled
        }
    }

    public void testCreateGameFailsIfNotEligible() {
        Set<Object> features = ["GameFeature.SystemPuzzles", "GameFeature.Thieving"]
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Player initiatingPlayer = PONE
        GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
        game.features.addAll(features)
        GameCoreTestCase.StringMPGame savedGame = new GameCoreTestCase.StringMPGame()
        savedGame.features = features
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findOne    : {
                    assert it == PONE.id
                    return PONE
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
        } catch (OutOfGamesForTodayException e) {
            //
        }
    }

    public void testInvalidInitiator() {
        Set<Object> features = ["1", 345, new HashMap()]
        List<Player> players = [PONE, PTWO, PTHREE]

        String playerId = "unkw"
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return players
                },
                findOne    : {
                    assert it == playerId
                    return null
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleCreateNewGame(playerId, players.collect { it.md5 }, features)
            fail("Should have failed")
        } catch (FailedToFindPlayersException e) {
            //
        }
    }


    public void testNotAllPlayersFound() {
        Set<Object> features = [1.3, "X", 45]
        List<Player> players = [PONE, PTWO, PTHREE]
        Player initiatingPlayer = PFOUR
        handler.playerRepository = [
                findByMd5In: {
                    Iterable<String> it ->
                        assert it.collect { it } as Set == players.collect { it.md5 } as Set
                        return [PONE, PTHREE]
                },
                findOne    : {
                    assert it == PFOUR.id
                    return PFOUR
                }
        ] as AbstractPlayerRepository

        try {
            handler.handleCreateNewGame(initiatingPlayer.id, players.collect { it.md5 }, features)
            fail("Should have failed")
        } catch (FailedToFindPlayersException e) {
            //
        }
    }
}
