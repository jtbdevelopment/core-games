package com.jtbdevelopment.games.tracking

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 3/28/15
 * Time: 2:41 PM
 */
@CompileStatic
interface GameEligibilityTracker<RESULT extends PlayerGameEligibilityResult> {
    RESULT getGameEligibility(final Player player)

    void revertGameEligibility(final RESULT result)
}