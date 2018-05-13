package com.jtbdevelopment.games.players;

import java.beans.Transient;
import java.io.Serializable;

/**
 * Date: 1/30/15 Time: 6:56 PM
 */
public interface GameSpecificPlayerAttributes extends Serializable {

  @Transient
  Player getPlayer();

  @Transient
  void setPlayer(final Player player);
}
