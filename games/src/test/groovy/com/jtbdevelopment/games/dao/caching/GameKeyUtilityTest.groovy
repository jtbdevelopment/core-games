package com.jtbdevelopment.games.dao.caching

import com.jtbdevelopment.games.games.Game

/**
 * Date: 3/2/15
 * Time: 8:49 PM
 */
class GameKeyUtilityTest extends GroovyTestCase {
    void testCollectGameIDs() {
        assert GameKeyUtility.collectGameIDs(
                [
                        [getId: { 'G1' }] as Game,
                        [getId: { 'G2' }] as Game,
                        [getId: { 'XX' }] as Game
                ]
        ) as Set == ['G1', 'G2', 'XX'] as Set
    }

    void testCollectGameIDsNull() {
        assert GameKeyUtility.collectGameIDs(null).isEmpty()
    }
}
