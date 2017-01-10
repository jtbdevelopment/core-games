package com.jtbdevelopment.games.state

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

    void setVersion(final Integer version);

    TIMESTAMP getCreated()

    void setCreated(final TIMESTAMP created)

    TIMESTAMP getLastUpdate()

    void setLastUpdate(final TIMESTAMP lastUpdate)

    TIMESTAMP getCompletedTimestamp()

    void setCompletedTimestamp(final TIMESTAMP completed)

    Set<FEATURES> getFeatures()

    void setFeatures(final Set<FEATURES> features)

    Map<FEATURES, Object> getFeatureData()

    void setFeatureData(final Map<FEATURES, Object> featureData)

    GamePhase getGamePhase()

    void setGamePhase(final GamePhase gamePhase)
}