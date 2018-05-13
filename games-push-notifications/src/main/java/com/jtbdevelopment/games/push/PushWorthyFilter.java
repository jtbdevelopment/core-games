package com.jtbdevelopment.games.push;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import java.io.Serializable;

/**
 * Date: 10/10/2015 Time: 4:37 PM
 */
public interface PushWorthyFilter<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>> {

  boolean shouldPush(final P player, final IMPL game);
}
