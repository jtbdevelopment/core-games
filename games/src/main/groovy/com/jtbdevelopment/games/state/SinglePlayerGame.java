package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;
import groovy.transform.CompileStatic;
import java.io.Serializable;

/**
 * Date: 12/31/2014 Time: 5:16 PM
 */
@CompileStatic
public interface SinglePlayerGame<ID extends Serializable, TIMESTAMP, FEATURES> extends
    Game<ID, TIMESTAMP, FEATURES> {

  Player<ID> getPlayer();

  void setPlayer(final Player<ID> player);
}
