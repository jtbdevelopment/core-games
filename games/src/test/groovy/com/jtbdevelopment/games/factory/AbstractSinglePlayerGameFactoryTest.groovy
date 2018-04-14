package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringSPGame
import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.SinglePlayerGame

/**
 * Date: 4/4/2015
 * Time: 9:41 PM
 */
class AbstractSinglePlayerGameFactoryTest extends GameCoreTestCase {
    private
    static class TestSinglePlayerGameFactory extends AbstractSinglePlayerGameFactory<StringSPGame, Object> {
        TestSinglePlayerGameFactory(List<GameInitializer<StringSPGame>> gameInitializers, List<GameValidator<StringSPGame>> gameValidators) {
            super(gameInitializers, gameValidators)
        }

        @Override
        protected StringSPGame newGame() {
            return new StringSPGame()
        }
    }
    TestSinglePlayerGameFactory gameFactory

    void testCreatingNewGame() {
        int validatorsCalled = 0
        int initializersCalled = 0
        def initializer = [initializeGame: { initializersCalled++ }] as GameInitializer
        def validator = [
                validateGame: { validatorsCalled++; true },
                errorMessage: {
                    fail("Should not be called")
                }] as GameValidator

        gameFactory = new TestSinglePlayerGameFactory([initializer, initializer, initializer, initializer], [validator, validator])


        Set<Object> expectedFeatures = ["1", 2] as Set
        SinglePlayerGame game = gameFactory.createGame(expectedFeatures, PONE)

        assertNotNull game
        assert validatorsCalled == 2
        assert initializersCalled == 4
        assert game.features == expectedFeatures
        assert game.player == PONE
        assert game.lastUpdate == game.created
        assert game.created == null
        assert GamePhase.Setup == game.gamePhase
        assertNull game.version
    }


    void testCreatingRematchGame() {
        int validatorsCalled = 0
        int initializersCalled = 0
        def initializer = [initializeGame: { initializersCalled++ }] as GameInitializer
        def validator = [
                validateGame: { validatorsCalled++; true },
                errorMessage: {
                    fail("Should not be called")
                }] as GameValidator

        gameFactory = new TestSinglePlayerGameFactory([initializer, initializer, initializer, initializer], [validator, validator])

        Set<Object> expectedFeatures = [32.1, new StringBuilder()] as Set

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
        assert GamePhase.Setup == game.gamePhase
        assertNull game.version
        assert game.round == (priorGame.round + 1)
        assert game.previousId == priorGame.id
    }


    void testErrorOnValidationFail() {
        int validatorsCalled = 0
        def validator = [
                validateGame: { validatorsCalled++; false },
                errorMessage: {
                    "TADA!"
                }] as GameValidator

        gameFactory = new TestSinglePlayerGameFactory([], [validator, validator])


        Set<Object> expectedFeatures = [54, 55, "56"] as Set

        StringSPGame priorGame = new StringSPGame();
        priorGame.features = expectedFeatures
        priorGame.player = PONE
        try {
            gameFactory.createGame(priorGame)
            fail("Should have failed")
        } catch (FailedToCreateValidGameException e) {
            assert validatorsCalled == 2
            assert e.message == "System failed to create a valid game.  TADA!  TADA!"
        }
    }
}
