package com.jtbdevelopment.games;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.AbstractGame;
import java.util.List;

/**
 * Date: 11/8/14 Time: 9:09 AM
 */
public class StringGame extends AbstractGame<String, Object> {

    private String id;
    private String previousId;

    @Override
    public String getIdAsString() {
        return id;
    }

    @Override
    public String getPreviousIdAsString() {
        return previousId;
    }

    @Override
    public List<Player<String>> getAllPlayers() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreviousId() {
        return previousId;
    }

    public void setPreviousId(String previousId) {
        this.previousId = previousId;
    }
}
