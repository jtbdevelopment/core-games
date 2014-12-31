package com.jtbdevelopment.games.players

import com.jtbdevelopment.games.GameCoreTestCase

/**
 * Date: 11/26/14
 * Time: 9:00 PM
 */
class PlayerMaskerTest extends GameCoreTestCase {
    PlayerMasker masker = new PlayerMasker();

    void testMaskFriends() {
        assert masker.maskFriends([PONE, PTWO, PTHREE] as Set) == [
                (PONE.md5)  : PONE.displayName,
                (PTWO.md5)  : PTWO.displayName,
                (PTHREE.md5): PTHREE.displayName
        ]
    }
}
