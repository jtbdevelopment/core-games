package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerMasker

/**
 * Date: 11/26/14
 * Time: 4:18 PM
 */
class AbstractFriendFinderTest extends GameCoreTestCase {
    AbstractFriendFinder<String> finder = new AbstractFriendFinder<String>() {};

    void testSumOfSourceBasedFinders() {
        def f1 = [
                handlesSource: {
                    String it ->
                        true;
                },
                findFriends  : {
                    StringPlayer p ->
                        assert p.is(PFOUR)
                        return [(SourceBasedFriendFinder.FRIENDS_KEY): [PONE, PTWO] as Set, 'Y': ['Yes'] as Set]
                }
        ] as SourceBasedFriendFinder
        def f2 = [
                handlesSource: {
                    String it ->
                        true;
                },
                findFriends  : {
                    StringPlayer p ->
                        assert p.is(PFOUR)
                        return [(SourceBasedFriendFinder.FRIENDS_KEY): [PONE, PTHREE] as Set, 'X': [1, 2, 3] as Set]
                }
        ] as SourceBasedFriendFinder
        def f3 = [
                handlesSource: {
                    String it ->
                        false;
                }
        ] as SourceBasedFriendFinder

        finder.friendFinders = [f1, f2, f3]
        finder.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PFOUR.id
                        return PFOUR
                }
        ] as AbstractPlayerRepository<String>
        def masked = ['x': 'y', '1': '2']
        finder.friendMasker = [
                maskFriends: {
                    Set<Player> friends ->
                        assert friends == [PONE, PTWO, PTHREE] as Set
                        return masked
                }
        ] as PlayerMasker

        assert finder.findFriends(PFOUR.id) == [
                (SourceBasedFriendFinder.MASKED_FRIENDS_KEY): masked,
                'Y'                                         : ['Yes'] as Set,
                'X'                                         : [1, 2, 3] as Set
        ]
    }

    void testNoPlayerInRepository() {
        finder.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PFOUR.id
                        return null
                }
        ] as AbstractPlayerRepository<String>

        try {
            finder.findFriends(PFOUR.id)
            fail("should have failed")
        } catch (FailedToFindPlayersException e) {
            //
        }
    }

    void testDisabledPlayer() {
        finder.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PINACTIVE1.id
                        return PINACTIVE1
                }
        ] as AbstractPlayerRepository<String>

        try {
            finder.findFriends(PINACTIVE1.id)
            fail("should have failed")
        } catch (FailedToFindPlayersException e) {
            //
        }
    }
}