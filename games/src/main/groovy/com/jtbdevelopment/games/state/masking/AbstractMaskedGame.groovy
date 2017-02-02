package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic

import java.beans.Transient

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
@CompileStatic
abstract class AbstractMaskedGame<FEATURES> implements MaskedGame<FEATURES> {
    String id
    String previousId
    Integer version

    int round

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
    @Transient
    String getIdAsString() {
        return id
    }

    @Override
    @Transient
    String getPreviousIdAsString() {
        return previousId
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    void setIdAsString(final String id) {

    }

    @Override
    List<Player<String>> getAllPlayers() {
        null
    }
}
