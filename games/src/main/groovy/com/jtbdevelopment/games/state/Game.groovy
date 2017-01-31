package com.jtbdevelopment.games.state

import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 12/31/2014
 * Time: 4:56 PM
 */
@CompileStatic
interface Game<ID extends Serializable, TIMESTAMP, FEATURES> {
    ID getId()
    void setId(final ID id)
    String getIdAsString()

    Integer getVersion()
    void setVersion(final Integer version)

    ID getPreviousId()
    void setPreviousId(final ID previousID)

    String getPreviousIdAsString()

    int getRound()
    void setRound(final int round)

    TIMESTAMP getCreated()
    void setCreated(final TIMESTAMP created)

    TIMESTAMP getLastUpdate()
    void setLastUpdate(final TIMESTAMP lastUpdate)

    TIMESTAMP getCompletedTimestamp()
    void setCompletedTimestamp(final TIMESTAMP completed)

    Set<FEATURES> getFeatures()
    void setFeatures(final Set<FEATURES> features)

    @Deprecated
    Map<FEATURES, Object> getFeatureData()

    @Deprecated
    void setFeatureData(final Map<FEATURES, Object> featureData)

    GamePhase getGamePhase()
    void setGamePhase(final GamePhase gamePhase)

    //  Convenience for single and multi player games to be handled by same classes
    List<Player<ID>> getAllPlayers()
}