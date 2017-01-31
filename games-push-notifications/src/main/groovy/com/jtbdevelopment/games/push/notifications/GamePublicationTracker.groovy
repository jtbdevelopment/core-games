package com.jtbdevelopment.games.push.notifications

import groovy.transform.CompileStatic

/**
 * Date: 10/10/2015
 * Time: 4:24 PM
 */
@CompileStatic
class GamePublicationTracker implements Serializable {
    Serializable pid
    Serializable gid

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        GamePublicationTracker tracker = (GamePublicationTracker) o

        if (gid != tracker.gid) return false
        if (pid != tracker.pid) return false

        return true
    }

    int hashCode() {
        int result
        result = pid.hashCode()
        result = 31 * result + gid.hashCode()
        return result
    }

    @Override
    String toString() {
        return "GamePublicationTracker{" +
                "pid=" + pid +
                ", gid=" + gid +
                '}'
    }
}
