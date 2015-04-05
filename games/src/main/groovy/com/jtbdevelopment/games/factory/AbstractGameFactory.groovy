package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.exceptions.input.FailedToCreateValidGameException
import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 4/4/2015
 * Time: 8:43 PM
 */
@CompileStatic
abstract class AbstractGameFactory<IMPL extends Game> {
    @Autowired
    List<GameInitializer> gameInitializers = []
    @Autowired
    List<GameValidator> gameValidators = []

    protected abstract IMPL newGame()

    protected void copyFromPreviousGame(final IMPL previousGame, final IMPL newGame) {
    }

    protected IMPL prepareGame(final IMPL game) {
        initializeGame(game)
        validateGame(game)
        game
    }

    protected void initializeGame(final IMPL game) {
        gameInitializers.each {
            GameInitializer it ->
                it.initializeGame(game)
        }
    }

    protected void validateGame(final IMPL game) {
        Collection<GameValidator> invalid = gameValidators.findAll { GameValidator it -> !it.validateGame(game) }
        if (!invalid.empty) {
            StringBuilder error = new StringBuilder()
            invalid.each { GameValidator gameValidator -> error.append(gameValidator.errorMessage()).append("  ") }
            throw new FailedToCreateValidGameException(error.toString())
        }
    }
}
