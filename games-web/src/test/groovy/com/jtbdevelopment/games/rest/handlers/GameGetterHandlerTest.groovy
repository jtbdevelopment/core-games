package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame
import com.jtbdevelopment.games.state.masking.GameMasker

/**
 * Date: 11/17/14
 * Time: 6:41 AM
 */
class GameGetterHandlerTest extends GameCoreTestCase {
    GameGetterHandler handler = new GameGetterHandler()

    private final GameCoreTestCase.StringMPGame game = new GameCoreTestCase.StringMPGame()
    private final String gameId = "G-id"

    void testHandlerBasicMultiPlayerWithMasking() {
        game.players = [PTWO, PONE]

        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(game)
                },
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        AbstractMaskedMultiPlayerGame maskedGame = new AbstractMaskedMultiPlayerGame() {}
        handler.gameMasker = [
                maskGameForPlayer: {
                    Game g, GameCoreTestCase.StringPlayer p ->
                        assert g.is(game)
                        assert p.is(PONE)
                        return maskedGame
                }
        ] as GameMasker

        assert maskedGame.is(handler.getGame(PONE.id, gameId))
    }

    void testHandlerBasicMultiPlayerWithoutMasking() {
        game.players = [PTWO, PONE]

        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(game)
                },
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        assert game.is(handler.getGame(PONE.id, gameId))
    }

    void testHandlerBasicSinglePlayer() {
        GameCoreTestCase.StringGame game = new GameCoreTestCase.StringGame()
        AbstractMaskedSinglePlayerGame maskedGame = new AbstractMaskedSinglePlayerGame() {}

        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(game)
                },
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository
        handler.gameMasker = [
                maskGameForPlayer: {
                    Game g, GameCoreTestCase.StringPlayer p ->
                        assert g.is(game)
                        assert p.is(PONE)
                        return maskedGame
                }
        ] as GameMasker

        assert maskedGame.is(handler.getGame(PONE.id, gameId))
    }

    void testHandlerBasicSinglePlayerWithoutMasking() {
        GameCoreTestCase.StringGame game = new GameCoreTestCase.StringGame()

        handler.gameRepository = [
                findById: {
                    String it ->
                        assert it == gameId
                        return Optional.of(game)
                },
        ] as AbstractGameRepository
        handler.playerRepository = [
                findById: {
                    String it ->
                        assert it == PONE.id
                        return Optional.of(PONE)
                }
        ] as AbstractPlayerRepository

        assert game.is(handler.getGame(PONE.id, gameId))
    }
}
