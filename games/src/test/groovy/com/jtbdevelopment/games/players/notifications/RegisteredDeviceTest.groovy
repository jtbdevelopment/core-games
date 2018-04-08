package com.jtbdevelopment.games.players.notifications

import java.time.Instant

/**
 * Date: 10/16/15
 * Time: 6:54 AM
 */
class RegisteredDeviceTest extends GroovyTestCase {
    RegisteredDevice device1 = new RegisteredDevice(deviceID: "X12345")
    RegisteredDevice device2 = new RegisteredDevice(deviceID: device1.deviceID)
    RegisteredDevice device3 = new RegisteredDevice(deviceID: "X12345")
    RegisteredDevice device4 = new RegisteredDevice(deviceID: "4hjx")

    void testConstructorDevice() {
        Instant start = Instant.now()
        RegisteredDevice defaultDevice = new RegisteredDevice()
        Instant end = Instant.now()

        assert "" == defaultDevice.deviceID
        assert start <= defaultDevice.lastRegistered
        assert end >= defaultDevice.lastRegistered
    }

    void testEquals() {
        assert device1 == device2
        assert device1 == device3
        assert device2 == device3
        assert device1 != device4
        assert device3 != device4
    }

    void testHashCode() {
        assert device1.deviceID.hashCode() == device1.hashCode()
        assert device1.hashCode() == device2.hashCode()
        assert device1.hashCode() == device3.hashCode()
        assert device2.hashCode() == device3.hashCode()
        assert device1.hashCode() != device4.hashCode()
        assert device3.hashCode() != device4.hashCode()

    }
}
