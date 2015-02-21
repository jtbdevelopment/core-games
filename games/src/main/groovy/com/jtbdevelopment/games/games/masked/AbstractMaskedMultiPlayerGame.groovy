package com.jtbdevelopment.games.games.masked

import com.jtbdevelopment.games.games.PlayerState
import groovy.transform.CompileStatic

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
@CompileStatic
abstract class AbstractMaskedMultiPlayerGame<FEATURES> implements MaskedMultiPlayerGame<FEATURES> {
    String maskedForPlayerID
    String maskedForPlayerMD5

    String id
    Integer version

    Long created
    Long lastUpdate
    Long completedTimestamp
    Long declinedTimestamp

    String initiatingPlayer
    Map<String, String> players = [:]  //  players will be hashed down to an md5 key + displayName
    Map<String, PlayerState> playerStates = [:]  // key will be md5 key
    Map<String, String> playerImages = [:] // key will be md5
    Map<String, String> playerProfiles = [:] // key will be md5

    Set<FEATURES> features = [] as Set
    Map<FEATURES, Object> featureData = [:]

    @Override
    String getIdAsString() {
        return id
    }

    void setIdAsString(final String id) {

    }
}
