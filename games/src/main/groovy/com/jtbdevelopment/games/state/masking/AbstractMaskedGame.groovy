package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
@CompileStatic
abstract class AbstractMaskedGame<FEATURES> implements MaskedGame<FEATURES> {
    String id
    Integer version

    Long created
    Long lastUpdate
    Long completedTimestamp

    GamePhase gamePhase

    Map<String, String> players = [:]  //  players will be hashed down to an md5 key + displayName
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
