package com.jtbdevelopment.games.tracking

import com.jtbdevelopment.games.players.Player

/**
 * Date: 3/28/15
 * Time: 2:41 PM
 */
interface GameEligibilityTracker<RESULT extends GameEligibilityResult> {
    RESULT getGameEligibility(final Player player)

    void revertGameEligibility(final RESULT result)
}