package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException
import com.jtbdevelopment.games.state.SinglePlayerGame

/**
 * Date: 4/4/2015
 * Time: 9:41 PM
 */
class AbstractSinglePlayerGameFactoryTest extends GameCoreTestCase {
    AbstractSinglePlayerGameFactory gameFactory = new AbstractSinglePlayerGameFactory<GameCoreTestCase.StringSPGame, Object>() {
        @Override
        protected GameCoreTestCase.StringSPGame newGame() {
            return new GameCoreTestCase.StringSPGame()
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
        Thread.sleep(1)
        SinglePlayerGame game = gameFactory.createGame(expectedFeatures, PONE)

        assertNotNull game
        assert validatorsCalled == 2
        assert initializersCalled == 4
        assert game.features == expectedFeatures
        assert game.player == PONE
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
        Thread.sleep(1)

        StringSPGame priorGame = new StringSPGame();
        priorGame.features = expectedFeatures
        priorGame.player = PTWO
        SinglePlayerGame game = gameFactory.createGame(priorGame)

        assertNotNull game
        assert validatorsCalled == 2
        assert initializersCalled == 4
        assert game.features == expectedFeatures
        assert game.player == PTWO
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

        StringSPGame priorGame = new StringSPGame();
        priorGame.features = expectedFeatures
        priorGame.player = PONE
        try {
            gameFactory.createGame(priorGame)
            fail("Should have failed")
        } catch (FailedToCreateValidGameException e) {
            assert validatorsCalled == 2
            assert e.message == "System failed to create a valid game.  TADA!  TADA!  "
        }
    }
}
