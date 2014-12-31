package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository

/**
 * Date: 11/26/14
 * Time: 1:12 PM
 */
class AbstractManualFriendFinderTest extends GameCoreTestCase {
    AbstractManualFriendFinder finder = new AbstractManualFriendFinder()

    void testHandlesSource() {
        assert finder.handlesSource(AbstractManualFriendFinder.MANUAL)
        assertFalse finder.handlesSource("Facebook")
    }

    void testFindFriends() {
        def playerA = makeSimplePlayer("a")
        def pX = makeSimplePlayer("b")
        def pY = makeSimplePlayer("c")
        def pZ = makeSimplePlayer("d")
        def ps = [pX, pY, pZ, playerA]
        finder.playerRepository = [
                findBySourceAndDisabled: {
                    String source, boolean disabled ->
                        assert source == AbstractManualFriendFinder.MANUAL
                        assertFalse disabled
                        return ps
                }
        ] as AbstractPlayerRepository<String>

        assert finder.findFriends(playerA) == [(SourceBasedFriendFinder.FRIENDS_KEY): [pX, pY, pZ] as Set]
    }
}
