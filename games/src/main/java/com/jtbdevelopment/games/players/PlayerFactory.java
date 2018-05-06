package com.jtbdevelopment.games.players;

import java.io.Serializable;

/**
 * Date: 12/30/2014 Time: 7:11 PM
 */
public interface PlayerFactory<ID extends Serializable, P extends Player<ID>> {

  P newPlayer();

  P newManualPlayer();

  P newSystemPlayer();
}
