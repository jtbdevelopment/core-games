package com.jtbdevelopment.games.players

import com.jtbdevelopment.games.GameCoreTestCase
import org.apache.commons.codec.digest.DigestUtils

/**
 * Date: 1/11/15
 * Time: 8:38 AM
 */
class AbstractPlayerTest extends GameCoreTestCase {

    void testInitializesDefaults() {
        Player p = new StringPlayer()
        assertFalse p.disabled
        assertFalse p.adminUser
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


}
