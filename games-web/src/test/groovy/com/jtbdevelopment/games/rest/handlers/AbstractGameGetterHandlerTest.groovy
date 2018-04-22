package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException
import com.jtbdevelopment.games.stringimpl.StringMPGame
import com.jtbdevelopment.games.stringimpl.StringSPGame

import static com.jtbdevelopment.games.GameCoreTestCase.*

/**
 * Date: 3/27/15
 * Time: 6:45 PM
 */
class AbstractGameGetterHandlerTest extends GroovyTestCase {
    AbstractGameGetterHandler handler = new AbstractGameGetterHandler() {}

    void testValidatePlayerForMultiPlayerGame() {
        StringMPGame mpGame = new StringMPGame()
        mpGame.players = [PONE, PTWO]
        handler.validatePlayerForGame(mpGame, PONE)
        handler.validatePlayerForGame(mpGame, PTWO)
    }

    void testInvalidValidatePlayerForMultiPlayerGame() {
        StringMPGame mpGame = new StringMPGame()
        mpGame.players = [PONE, PTWO]
        shouldFail(PlayerNotPartOfGameException.class, {
            handler.validatePlayerForGame(mpGame, PTHREE)
        })
    }

    void testValidatePlayerForSinglePlayerGame() {
        StringSPGame mpGame = new StringSPGame()
        mpGame.player = PONE
        handler.validatePlayerForGame(mpGame, PONE)
    }

    void testInvalidValidatePlayerForSinglePlayerGame() {
        StringSPGame mpGame = new StringSPGame()
        mpGame.player = PONE
        shouldFail(PlayerNotPartOfGameException.class, {
            handler.validatePlayerForGame(mpGame, PTHREE)
        })
    }

    void testLoadGame() {
        StringMPGame mpGame = new StringMPGame()
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
