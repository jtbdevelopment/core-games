package com.jtbdevelopment.games.games

import groovy.transform.CompileStatic
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version

import java.time.ZonedDateTime

/**
 * Date: 12/31/2014
 * Time: 5:25 PM
 */
@CompileStatic
abstract class AbstractGame<ID extends Serializable> implements Game<ID> {
    @Version
    Integer version

    @CreatedDate
    ZonedDateTime created

    @LastModifiedDate
    ZonedDateTime lastUpdate

    ZonedDateTime completedTimestamp

    boolean equals(final o) {
        if (this.is(o)) return true
        if (!(o instanceof Game)) return false

        final Game game = (Game) o

        if (id != game.id) return false

        return true
    }

    int hashCode() {
        return idAsString.hashCode()
    }
}
