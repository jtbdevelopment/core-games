package com.jtbdevelopment.games.state

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
abstract class AbstractGame<ID extends Serializable, FEATURES> implements Game<ID, ZonedDateTime, FEATURES>, Serializable {
    @Version
    Integer version

    @CreatedDate
    ZonedDateTime created

    @LastModifiedDate
    ZonedDateTime lastUpdate

    ZonedDateTime completedTimestamp

    GamePhase gamePhase

    Set<FEATURES> features = [] as Set
    Map<FEATURES, Object> featureData = [:]

    boolean equals(final o) {
        if (this.is(o)) return true
        if (!(o instanceof Game)) return false

        final Game game = (Game) o

        if (id != game.id) return false

        return true
    }

    int hashCode() {
        return idAsString ? idAsString.hashCode() : 0
    }
}
