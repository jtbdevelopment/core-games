package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer

/**
 * Date: 11/26/14
 * Time: 1:12 PM
 */
class ManualFriendFinderTest extends GameCoreTestCase {
    ManualFriendFinder finder = new ManualFriendFinder()

    void testHandlesSource() {
        assert finder.handlesSource(ManualPlayer.MANUAL_SOURCE)
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
                        assert source == ManualPlayer.MANUAL_SOURCE
                        assertFalse disabled
                        return ps
                }
        ] as AbstractPlayerRepository<String>

        assert finder.findFriends(playerA) == [(SourceBasedFriendFinder.FRIENDS_KEY): [pX, pY, pZ] as Set]
    }
}
