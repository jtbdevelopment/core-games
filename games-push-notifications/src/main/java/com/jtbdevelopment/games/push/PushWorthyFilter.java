package com.jtbdevelopment.games.push;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.MultiPlayerGame;

/**
 * Date: 10/10/2015 Time: 4:37 PM
 */
public interface PushWorthyFilter {

  public abstract boolean shouldPush(final Player player, final MultiPlayerGame game);
}
