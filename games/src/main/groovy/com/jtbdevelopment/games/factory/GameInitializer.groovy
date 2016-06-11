package com.jtbdevelopment.games.factory

import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic
import org.springframework.core.Ordered

/**
 * Date: 1/13/15
 * Time: 7:12 AM
 */
@CompileStatic
interface GameInitializer<IMPL extends Game> extends Ordered {
    @SuppressWarnings("GroovyUnusedDeclaration")
    static final int EARLY_ORDER = 1
    static final int DEFAULT_ORDER = 1000
    @SuppressWarnings("GroovyUnusedDeclaration")
    static final int LATE_ORDER = 10000

    void initializeGame(final IMPL game)
}