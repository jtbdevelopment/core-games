package com.jtbdevelopment.games.players;

/**
 * Date: 1/30/15 Time: 6:56 PM
 */
public interface GameSpecificPlayerAttributesFactory {

  GameSpecificPlayerAttributes newPlayerAttributes();

  GameSpecificPlayerAttributes newManualPlayerAttributes();

  GameSpecificPlayerAttributes newSystemPlayerAttributes();
}
