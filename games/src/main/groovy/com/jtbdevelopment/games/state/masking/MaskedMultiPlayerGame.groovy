package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic

/**
 * Date: 2/18/15
 * Time: 6:54 PM
 */
@CompileStatic
interface MaskedMultiPlayerGame<FEATURES> extends Game<String, Long, FEATURES> {
    int getRound()

    void setRound(final int round)

    String getMaskedForPlayerID()

    void setMaskedForPlayerID(final String maskedForPlayerID)

    String getMaskedForPlayerMD5()

    void setMaskedForPlayerMD5(final String maskedForPlayerMD5)

    Long getDeclinedTimestamp()

    void setDeclinedTimestamp(final Long declinedTimestamp)

    Long getRematchTimestamp()

    void setRematchTimestamp(final Long rematchTimestamp)

    String getInitiatingPlayer()

    void setInitiatingPlayer(final String initiatingPlayer)

    Map<String, String> getPlayers();

    void setPlayers(final Map<String, String> players)

    Map<String, PlayerState> getPlayerStates()

    void setPlayerStates(final Map<String, PlayerState> playerStates)

    Map<String, String> getPlayerImages();

    void setPlayerImages(final Map<String, String> playerImages)

    Map<String, String> getPlayerProfiles();

    void setPlayerProfiles(final Map<String, String> players)

    GamePhase getGamePhase()

    void setGamePhase(final GamePhase gamePhase)
}