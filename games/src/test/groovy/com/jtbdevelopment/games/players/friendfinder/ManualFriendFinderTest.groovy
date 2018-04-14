package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 11/26/14
 * Time: 1:12 PM
 */
class ManualFriendFinderTest extends GameCoreTestCase {
    AbstractPlayerRepository abstractPlayerRepository = mock(AbstractPlayerRepository.class)
    ManualFriendFinder finder = new ManualFriendFinder(abstractPlayerRepository)

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
        when(abstractPlayerRepository.findBySourceAndDisabled(ManualPlayer.MANUAL_SOURCE, false)).thenReturn(ps)
        assert finder.findFriends(playerA) == [(SourceBasedFriendFinder.FRIENDS_KEY): [pX, pY, pZ] as Set]
    }
}
