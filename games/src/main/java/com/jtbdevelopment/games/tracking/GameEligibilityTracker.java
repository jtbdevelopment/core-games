package com.jtbdevelopment.games.tracking;

import com.jtbdevelopment.games.players.Player;

/**
 * Date: 3/28/15 Time: 2:41 PM
 */
public interface GameEligibilityTracker<RESULT extends PlayerGameEligibilityResult> {

  RESULT getGameEligibility(final Player player);

  void revertGameEligibility(final RESULT result);
}
