package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException

/**
 * Date: 3/27/15
 * Time: 6:45 PM
 */
class AbstractGameGetterHandlerTest extends GameCoreTestCase {
    AbstractGameGetterHandler handler = new AbstractGameGetterHandler() {}

    void testValidatePlayerForMultiPlayerGame() {
        GameCoreTestCase.StringMPGame mpGame = new GameCoreTestCase.StringMPGame()
        mpGame.players = [PONE, PTWO]
        handler.validatePlayerForGame(mpGame, PONE)
        handler.validatePlayerForGame(mpGame, PTWO)
    }

    void testInvalidValidatePlayerForMultiPlayerGame() {
        GameCoreTestCase.StringMPGame mpGame = new GameCoreTestCase.StringMPGame()
        mpGame.players = [PONE, PTWO]
        shouldFail(PlayerNotPartOfGameException.class, {
            handler.validatePlayerForGame(mpGame, PTHREE)
        })
    }

    void testValidatePlayerForSinglePlayerGame() {
        GameCoreTestCase.StringSPGame mpGame = new GameCoreTestCase.StringSPGame()
        mpGame.player = PONE
        handler.validatePlayerForGame(mpGame, PONE)
    }

    void testInvalidValidatePlayerForSinglePlayerGame() {
        GameCoreTestCase.StringSPGame mpGame = new GameCoreTestCase.StringSPGame()
        mpGame.player = PONE
        shouldFail(PlayerNotPartOfGameException.class, {
            handler.validatePlayerForGame(mpGame, PTHREE)
        })
    }

    void testLoadGame() {
        GameCoreTestCase.StringMPGame mpGame = new GameCoreTestCase.StringMPGame()
        String id = 'X'
        handler.gameRepository = [
                findById: {
                    String i ->
                        assert id == i
                        Optional.of(mpGame)
                }
        ] as AbstractGameRepository
        assert mpGame.is(handler.loadGame(id))
    }

    void testLoadGameFailed() {
        String id = 'X'
        handler.gameRepository = [
                findById: {
                    String i ->
                        assert id == i
                        Optional.empty()
                }
        ] as AbstractGameRepository
        shouldFail(FailedToFindGameException.class, {
            handler.loadGame(id)
        })
    }

}
