package com.jtbdevelopment.games.games.masked

import com.jtbdevelopment.games.games.Game
import com.jtbdevelopment.games.games.PlayerState

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
abstract class AbstractMaskedMultiPlayerGame implements Game<String, Long> {
    String maskedForPlayerID
    String maskedForPlayerMD5

    String id
    Integer version

    Long created
    Long lastUpdate
    Long completedTimestamp

    String initiatingPlayer
    Map<String, String> players = [:]  //  players will be hashed down to an md5 key + displayName
    Map<String, PlayerState> playerStates = [:]  // key will be md5 key
    Map<String, String> playerImages = [:] // key will be md5
    Map<String, String> playerProfiles = [:] // key will be md5

    Long declinedTimestamp

    @Override
    String getIdAsString() {
        return id
    }
}
