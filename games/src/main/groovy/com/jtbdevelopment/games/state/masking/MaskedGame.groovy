package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.state.Game
import groovy.transform.CompileStatic

/**
 * Date: 2/18/15
 * Time: 6:54 PM
 */
@CompileStatic
interface MaskedGame<FEATURES> extends Game<String, Long, FEATURES> {
    int getRound()

    void setRound(final int round)

    Map<String, String> getPlayers()

    void setPlayers(final Map<String, String> players)

    Map<String, String> getPlayerImages()

    void setPlayerImages(final Map<String, String> playerImages)

    Map<String, String> getPlayerProfiles()

    void setPlayerProfiles(final Map<String, String> players)
}