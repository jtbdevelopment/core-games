package com.jtbdevelopment.games.games

import groovy.transform.CompileStatic

/**
 * Date: 12/31/2014
 * Time: 4:56 PM
 */
@CompileStatic
interface Game<ID extends Serializable, TIMESTAMP> {
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
}