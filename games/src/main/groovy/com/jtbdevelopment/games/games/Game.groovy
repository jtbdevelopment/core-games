package com.jtbdevelopment.games.games

import groovy.transform.CompileStatic

import java.time.ZonedDateTime

/**
 * Date: 12/31/2014
 * Time: 4:56 PM
 */
@CompileStatic
interface Game<ID extends Serializable> {
    ID getId()

    void setId(final ID id)

    String getIdAsString()

    Integer getVersion()

    void setVersion(final Integer version);

    ZonedDateTime getCreated()

    void setCreated(final ZonedDateTime created)

    ZonedDateTime getLastUpdate()

    void setLastUpdate(final ZonedDateTime lastUpdate)

    ZonedDateTime getCompletedTimestamp()

    void setCompletedTimestamp(final ZonedDateTime completed)
}