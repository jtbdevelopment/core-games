package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Date: 12/31/2014
 * Time: 5:07 PM
 * <p>
 * A MultiPlayerGame may still be played by a single player
 * It just has the potential to be played by more than one
 */
public interface MultiPlayerGame<ID extends Serializable, TIMESTAMP, FEATURES> extends Game<ID, TIMESTAMP, FEATURES> {
    ID getInitiatingPlayer();

    void setInitiatingPlayer(final ID initiatingPlayer);

    List<Player<ID>> getPlayers();

    void setPlayers(final List<Player<ID>> players);

    Map<ID, PlayerState> getPlayerStates();

    void setPlayerStates(final Map<ID, PlayerState> playerState);

    TIMESTAMP getDeclinedTimestamp();

    void setDeclinedTimestamp(final TIMESTAMP declinedTimestamp);

    TIMESTAMP getRematchTimestamp();

    void setRematchTimestamp(final TIMESTAMP rematchTimestamp);
}
