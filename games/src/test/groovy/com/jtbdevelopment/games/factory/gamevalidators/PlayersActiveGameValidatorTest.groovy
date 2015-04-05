package com.jtbdevelopment.games.factory.gamevalidators

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository

/**
 * Date: 4/4/2015
 * Time: 8:36 PM
 */
class PlayersActiveGameValidatorTest extends GameCoreTestCase {
    PlayersActiveGameValidator validator = new PlayersActiveGameValidator()


    public void testPassesAllActivesForMPGame() {
        StringMPGame game = new StringMPGame()
        game.players = [PONE, PTWO]
        validator.playerRepository = [
                findAll: {
                    Iterable<String> input ->
                        assert input.collect { it } as Set == [PONE.id, PTWO.id] as Set
                        return [PONE, PTWO]
                },
        ] as AbstractPlayerRepository
        assert validator.validateGame(game)
    }

    public void testFailsActiveCountForMPGame() {
        StringMPGame game = new StringMPGame()
        game.players = [PONE, PTWO]
        validator.playerRepository = [
                findAll: {
                    Iterable<String> input ->
                        assert input.collect { it } as Set == [PONE.id, PTWO.id] as Set
                        return [PONE]
                },
        ] as AbstractPlayerRepository
        assertFalse validator.validateGame(game)
    }

    public void testFailsAnInactivePlayerForMPGame() {
        StringMPGame game = new StringMPGame()
        game.players = [PONE, PINACTIVE2]
        validator.playerRepository = [
                findAll: {
                    Iterable<String> input ->
                        assert input.collect { it } as Set == [PONE.id, PINACTIVE2.id] as Set
                        return [PONE, PINACTIVE2]
                },
        ] as AbstractPlayerRepository
        assertFalse validator.validateGame(game)
    }


    public void testPassesAllActivesForSPGame() {
        StringSPGame game = new StringSPGame()
        game.player = PONE
        validator.playerRepository = [
                findOne: {
                    String id ->
                        assert id == PONE.id
                        return PONE
                },
        ] as AbstractPlayerRepository
        assert validator.validateGame(game)
    }


    public void testFailsAnInactivePlayerForSPGame() {
        StringSPGame game = new StringSPGame()
        game.player = PINACTIVE1
        validator.playerRepository = [
                findOne: {
                    String id ->
                        assert id == PINACTIVE1.id
                        return PINACTIVE1
                },
        ] as AbstractPlayerRepository
        assertFalse validator.validateGame(game)
    }

    public void testFailsAnInactivePlayerViaNotLoadingForSPGame() {
        StringSPGame game = new StringSPGame()
        game.player = PINACTIVE1
        validator.playerRepository = [
                findOne: {
                    String id ->
                        assert id == PINACTIVE1.id
                        return null
                },
        ] as AbstractPlayerRepository
        assertFalse validator.validateGame(game)
    }

    public void testErrorMessage() {
        assert validator.errorMessage() == "Game contains inactive players."
    }
}
