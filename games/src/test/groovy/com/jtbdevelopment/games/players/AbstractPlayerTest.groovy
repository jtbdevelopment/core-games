package com.jtbdevelopment.games.players

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.players.notifications.RegisteredDevice
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.data.annotation.Transient

import java.time.Instant

/**
 * Date: 1/11/15
 * Time: 8:38 AM
 */
class AbstractPlayerTest extends GameCoreTestCase {

    void testInitializesDefaults() {
        Instant start = Instant.now()
        Thread.sleep(100)
        Player p = new StringPlayer()
        assertFalse p.disabled
        assertFalse p.adminUser
        assert start < p.created
        assert start.minusSeconds(365 * 24 * 60 * 60) < p.lastLogin
        assert p.lastLogin <= Instant.now()
        assert p.created <= Instant.now()
        assert p.registeredDevices.empty
    }

    void testHashCodeWithNoId() {
        Player p = new StringPlayer()
        assert p.hashCode() == 0
    }

    void testHashCodeWithId() {
        assert PONE.hashCode() == PONE.id.hashCode()
    }

    void testIdAsStringWithNoId() {
        Player p = new StringPlayer()
        assertNull p.idAsString
    }

    void testSourceCannotBeChanged() {
        def SOURCE = "SOURCE"
        Player p = new StringPlayer(source: SOURCE)
        assert SOURCE == p.source
        p.setSource(SOURCE + "X")
        assert SOURCE == p.source
    }

    void testMd5IsCombinationOfCorrectFields() {
        String key = PONE.idAsString + PONE.source + PONE.displayName + PONE.sourceId
        def md5 = DigestUtils.md5Hex(key)
        assert PONE.md5 == md5
    }

    void testMd5IsBlankUntilAllFieldSet() {
        Player p = new StringPlayer()
        assert p.md5 == ''
        p.id = 'X'
        assert p.md5 == ''
        p.displayName = 'Y'
        assert p.md5 == ''
        p.source = 'S'
        assert p.getMd5() == ''
        p.sourceId = 'ID'
        assert p.getMd5() == DigestUtils.md5Hex('XSYID')
    }

    void testEquals() {
        assert PONE.equals(PONE)
        assertFalse PONE.equals(PTWO)
        assert PONE.equals(new StringPlayer(id: PONE.id))
        assertFalse PONE.equals("String")
        assertFalse PONE.equals(null)
    }

    void testToString() {
        assert new StringPlayer(
                id: 'XYZ',
                disabled: false,
                displayName: "BAYMAX",
                sourceId: "BAYMAX",
                source: "BIG HERO 6").toString() == "Player{id='XYZ', source='BIG HERO 6', sourceId='BAYMAX', displayName='BAYMAX', disabled=false}"
    }

    void testSourceAndSourceIdString() {
        assert new StringPlayer(
                id: 'XYZ',
                disabled: false,
                displayName: "BAYMAX",
                sourceId: "BAYMAX",
                source: "BIG HERO 6").getSourceAndSourceId() == 'BIG HERO 6/BAYMAX'
    }

    void testSourceAndSourceIdStringWhenOneIsNull() {
        assert new StringPlayer(
                id: 'XYZ',
                disabled: false,
                displayName: "BAYMAX",
                sourceId: null,
                source: "BIG HERO 6").getSourceAndSourceId() == null
        assert new StringPlayer(
                id: 'XYZ',
                disabled: false,
                displayName: "BAYMAX",
                sourceId: "BAYMAX",
                source: null).getSourceAndSourceId() == null

    }

    private class TestPlayerAttributes implements GameSpecificPlayerAttributes {
        @Transient
        Player player

        int someAttribute
    }

    void testSettingGameSpecificAttributeAlsoLinksAttributeBackToPlayer() {
        TestPlayerAttributes attributes = new TestPlayerAttributes(someAttribute: 5)

        assertNull attributes.player

        Player p = new StringPlayer(gameSpecificPlayerAttributes: attributes)
        assert p.is(attributes.player)
    }

    void testSettingGameSpecificAttributeToNullDoesNotException() {
        Player p = new StringPlayer()
        p.gameSpecificPlayerAttributes = null
        assertNull p.gameSpecificPlayerAttributes
    }

    void testUpdatingDeviceNotInSetAlready() {
        Player p = new StringPlayer()
        RegisteredDevice device = new RegisteredDevice(deviceID: "X")
        p.updateRegisteredDevice(device)
        assert [device] as Set == p.registeredDevices
    }

    void testUpdatingExistingDevice() {
        Player p = new StringPlayer()
        RegisteredDevice device = new RegisteredDevice(deviceID: "X")
        p.updateRegisteredDevice(device)

        RegisteredDevice updatedDevice = new RegisteredDevice(
                deviceID: device.deviceID,
                lastRegistered: device.lastRegistered.plusSeconds(1)
        )

        p.updateRegisteredDevice(updatedDevice)

        assert [updatedDevice] as Set == p.registeredDevices
        assert updatedDevice.lastRegistered == p.registeredDevices.iterator().next().lastRegistered
    }

    void testRemovingAnExistingDevice() {
        Player p = new StringPlayer()
        RegisteredDevice device = new RegisteredDevice(deviceID: "X", lastRegistered: Instant.now())
        p.updateRegisteredDevice(device)

        assertTrue p.registeredDevices.contains(device)

        RegisteredDevice remove = new RegisteredDevice(deviceID: "X", lastRegistered: Instant.now())
        p.removeRegisteredDevice(remove)
        assertFalse p.registeredDevices.contains(device)
    }
}
