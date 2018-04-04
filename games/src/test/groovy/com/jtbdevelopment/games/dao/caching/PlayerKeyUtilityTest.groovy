package com.jtbdevelopment.games.dao.caching

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player

/**
 * Date: 3/2/15
 * Time: 6:28 PM
 */
class PlayerKeyUtilityTest extends GroovyTestCase {
    PlayerKeyUtility playerKeyUtility = new PlayerKeyUtility()

    void testCollectPlayerIDs() {
        assert PlayerKeyUtility.collectPlayerIDs([
                [getId: { 'X' }] as Player,
                [getId: { '1' }] as Player,
                [getId: { 'B3' }] as Player
        ] as Set) as Set == ['X', '1', 'B3'] as Set
    }

    void testCollectPlayerIDsWithNull() {
        assert PlayerKeyUtility.collectPlayerIDs(null).isEmpty()
    }

    void testCollectPlayerMD5s() {
        assert PlayerKeyUtility.collectPlayerMD5s([
                [getMd5: { 'X' }] as Player,
                [getMd5: { '1' }] as Player,
                [getMd5: { 'B3' }] as Player
        ] as Set) as Set == ['X', '1', 'B3'] as Set
    }

    void testCollectPlayerMD5sWithNull() {
        assert PlayerKeyUtility.collectPlayerMD5s(null).isEmpty()
    }

    void testCollectPlayerSourceAndSourceIDs() {
        assert PlayerKeyUtility.collectPlayerSourceAndSourceIDs([
                [getSourceAndSourceId: { 'S1/X' }] as Player,
                [getSourceAndSourceId: { 'S2/1' }] as Player,
                [getSourceAndSourceId: { 'S3/B3' }] as Player
        ] as Set) as Set == ['S1/X', 'S2/1', 'S3/B3'] as Set
    }

    void testCollectPlayerSourceAndSourceIDsWithNull() {
        assert PlayerKeyUtility.collectPlayerSourceAndSourceIDs(null).isEmpty()
    }


    void testCollectSourceAndSourceIDs() {
        assert PlayerKeyUtility.collectSourceAndSourceIDs(
                'S1',
                ['X', 'Y', 'Z']
                        as Set) as Set == ['S1/X', 'S1/Y', 'S1/Z'] as Set
    }

    void testCollectSourceAndSourceIDsWithNull() {
        assert PlayerKeyUtility.collectSourceAndSourceIDs(null, null).isEmpty()
        assert PlayerKeyUtility.collectSourceAndSourceIDs('X', null).isEmpty()
        assert PlayerKeyUtility.collectSourceAndSourceIDs(null, ['X']).isEmpty()
    }

    void testMd5FromID() {
        String ID = 'ANID'
        String md5 = 'MD5'
        playerKeyUtility.playerRepository = [
                findById: {
                    Serializable id ->
                        assert id.is(ID)
                        return Optional.of([getMd5: { md5 }] as Player)
                }
        ] as AbstractPlayerRepository
        assert PlayerKeyUtility.md5FromID(ID) == md5
    }

    void testMd5FromIDNullResult() {
        String ID = 'ANID'
        playerKeyUtility.playerRepository = [
                findById: {
                    Serializable id ->
                        assert id.is(ID)
                        Optional.empty()
                }
        ] as AbstractPlayerRepository
        assertNull PlayerKeyUtility.md5FromID(ID)
    }

    void testSourceAndSourceIDFromID() {
        String ID = 'ANID'
        String ssid = 'XSID'
        playerKeyUtility.playerRepository = [
                findById: {
                    Serializable id ->
                        assert id.is(ID)
                        return Optional.of([getSourceAndSourceId: { ssid }] as Player)
                }
        ] as AbstractPlayerRepository
        assert PlayerKeyUtility.sourceAndSourceIDFromID(ID) == ssid
    }

    void testSourceAndSourceIDFromIDWithNull() {
        String ID = 'ANID'
        playerKeyUtility.playerRepository = [
                findById: {
                    Serializable id ->
                        assert id.is(ID)
                        return Optional.empty()
                }
        ] as AbstractPlayerRepository
        assertNull PlayerKeyUtility.sourceAndSourceIDFromID(ID)
    }
}
