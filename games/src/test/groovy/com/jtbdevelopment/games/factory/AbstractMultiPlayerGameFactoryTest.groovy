package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame

/**
 * Date: 4/4/2015
 * Time: 9:41 PM
 */
class AbstractMultiPlayerGameFactoryTest extends GameCoreTestCase {
    AbstractMultiPlayerGameFactory gameFactory = new AbstractMultiPlayerGameFactory<GameCoreTestCase.StringMPGame, Object>() {
        @Override
        protected GameCoreTestCase.StringMPGame newGame() {
            return new GameCoreTestCase.StringMPGame()
        }
    }

    public void testCreatingNewGame() {
        int validatorsCalled = 0
        int initializersCalled = 0
        def initializer = [initializeGame: { initializersCalled++ }] as GameInitializer
        def validator = [
                validateGame: { validatorsCalled++; true },
                errorMessage: {
                    fail("Should not be called")
                }] as GameValidator

        gameFactory.gameValidators = [validator, validator]
        gameFactory.gameInitializers = [initializer, initializer, initializer, initializer]


        Set<Object> expectedFeatures = ["1", 2] as Set
        Player initiatingPlayer = PONE
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Thread.sleep(1)
        MultiPlayerGame game = gameFactory.createGame(expectedFeatures, players, initiatingPlayer)

        assertNotNull game
        assert validatorsCalled == 2
        assert initializersCalled == 4
        assert game.features == expectedFeatures
        assert game.players == [PTWO, PTHREE, PFOUR, PONE]
        assert game.initiatingPlayer == initiatingPlayer.id
        assert game.lastUpdate == game.created
        assert game.created == null
        assertNull game.version
    }


    public void testCreatingRematchGame() {
        int validatorsCalled = 0
        int initializersCalled = 0
        def initializer = [initializeGame: { initializersCalled++ }] as GameInitializer
        def validator = [
                validateGame: { validatorsCalled++; true },
                errorMessage: {
                    fail("Should not be called")
                }] as GameValidator

        gameFactory.gameValidators = [validator, validator]
        gameFactory.gameInitializers = [initializer, initializer, initializer, initializer]

        Set<Object> expectedFeatures = [32.1, new StringBuilder()] as Set
        Player initiatingPlayer = PONE
        List<Player> players = [PTWO, PTHREE, PFOUR]
        Thread.sleep(1)

        StringMPGame priorGame = new StringMPGame();
        priorGame.features = expectedFeatures
        priorGame.players = players
        priorGame.players.add(PONE)
        priorGame.initiatingPlayer = PTHREE.id
        MultiPlayerGame game = gameFactory.createGame(priorGame, initiatingPlayer)

        assertNotNull game
        assert validatorsCalled == 2
        assert initializersCalled == 4
        assert game.features == expectedFeatures
        assert game.players == [PTHREE, PFOUR, PONE, PTWO]
        assert game.initiatingPlayer == initiatingPlayer.id
        assert game.lastUpdate == game.created
        assert game.created == null
        assertNull game.version
    }


    public void testErrorOnValidationFail() {
        int validatorsCalled = 0
        def validator = [
                validateGame: { validatorsCalled++; false },
                errorMessage: {
                    "TADA!"
                }] as GameValidator

        gameFactory.gameValidators = [validator, validator]


        Set<Object> expectedFeatures = [54, 55, "56"] as Set
        Player initiatingPlayer = PONE
        List<Player> players = [PTWO, PTHREE, PFOUR]

        StringMPGame priorGame = new StringMPGame();
        priorGame.features = expectedFeatures
        priorGame.players = players
        priorGame.players.add(PONE)
        priorGame.initiatingPlayer = PTHREE.id
        try {
            gameFactory.createGame(priorGame, initiatingPlayer)
            fail("Should have failed")
        } catch (FailedToCreateValidGameException e) {
            assert validatorsCalled == 2
            assert e.message == "System failed to create a valid game.  TADA!  TADA!  "
        }
    }
}
