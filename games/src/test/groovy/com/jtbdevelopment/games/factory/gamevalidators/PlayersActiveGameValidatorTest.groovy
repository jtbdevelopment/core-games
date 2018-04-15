package com.jtbdevelopment.games.factory.gamevalidators

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringMPGame
import com.jtbdevelopment.games.StringSPGame
import com.jtbdevelopment.games.dao.AbstractPlayerRepository

/**
 * Date: 4/4/2015
 * Time: 8:36 PM
 */
class PlayersActiveGameValidatorTest extends GameCoreTestCase {


    void testPassesAllActivesForMPGame() {
        StringMPGame game = new StringMPGame()
        game.players = [PONE, PTWO]
        def repository = [
                findAllById: {
                    Iterable<String> input ->
                        assert input.collect { it } as Set == [PONE.id, PTWO.id] as Set
                        return [PONE, PTWO]
                },
        ] as AbstractPlayerRepository
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository)
        assert validator.validateGame(game)
    }

    void testFailsActiveCountForMPGame() {
        StringMPGame game = new StringMPGame()
        game.players = [PONE, PTWO]
        def repository = [
                findAllById: {
                    Iterable<String> input ->
                        assert input.collect { it } as Set == [PONE.id, PTWO.id] as Set
                        return [PONE]
                },
        ] as AbstractPlayerRepository
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository)
        assertFalse validator.validateGame(game)
    }

    void testFailsAnInactivePlayerForMPGame() {
        StringMPGame game = new StringMPGame()
        game.players = [PONE, PINACTIVE2]
        def repository = [
                findAllById: {
                    Iterable<String> input ->
                        assert input.collect { it } as Set == [PONE.id, PINACTIVE2.id] as Set
                        return [PONE, PINACTIVE2]
                },
        ] as AbstractPlayerRepository
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository)
        assertFalse validator.validateGame(game)
    }


    void testPassesAllActivesForSPGame() {
        StringSPGame game = new StringSPGame()
        game.player = PONE
        def repository = [
                findById: {
                    String id ->
                        assert id == PONE.id
                        return Optional.of(PONE)
                },
        ] as AbstractPlayerRepository
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository)
        assert validator.validateGame(game)
    }


    void testFailsAnInactivePlayerForSPGame() {
        StringSPGame game = new StringSPGame()
        game.player = PINACTIVE1
        def repository = [
                findById: {
                    String id ->
                        assert id == PINACTIVE1.id
                        return Optional.of(PINACTIVE1)
                },
        ] as AbstractPlayerRepository
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository)
        assertFalse validator.validateGame(game)
    }

    void testFailsAnInactivePlayerViaNotLoadingForSPGame() {
        StringSPGame game = new StringSPGame()
        game.player = PINACTIVE1
        def repository = [
                findById: {
                    String id ->
                        assert id == PINACTIVE1.id
                        return Optional.empty()
                },
        ] as AbstractPlayerRepository
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(repository)
        assertFalse validator.validateGame(game)
    }

    void testErrorMessage() {
        PlayersActiveGameValidator validator = new PlayersActiveGameValidator(null)
        assert validator.errorMessage() == "Game contains inactive players."
    }
}
