package com.jtbdevelopment.games.players

import com.jtbdevelopment.games.GameCoreTestCase
import org.junit.Test

/**
 * Date: 11/26/14
 * Time: 9:00 PM
 */
class PlayerMaskerTest extends GameCoreTestCase {
    private PlayerMasker masker = new PlayerMasker();

    @Test
    void testMaskFriendsV2() {
        assert masker.maskFriendsV2([PONE, PTWO, PTHREE] as Set) == [
                [('md5'): PONE.md5, ('displayName'): PONE.displayName],
                [('md5'): PTWO.md5, ('displayName'): PTWO.displayName],
                [('md5'): PTHREE.md5, ('displayName'): PTHREE.displayName]
        ]
    }
}
