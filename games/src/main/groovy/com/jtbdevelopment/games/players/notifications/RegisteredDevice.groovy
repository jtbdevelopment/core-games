package com.jtbdevelopment.games.players.notifications

import org.springframework.stereotype.Component

import java.time.Instant

/**
 * Date: 10/16/15
 * Time: 6:52 AM
 */
@Component
class RegisteredDevice implements Serializable {
    String deviceID = ""
    Instant lastRegistered = Instant.now()

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final RegisteredDevice that = (RegisteredDevice) o

        if (deviceID != that.deviceID) return false

        return true
    }

    int hashCode() {
        return deviceID.hashCode()
    }
}
