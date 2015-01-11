package com.jtbdevelopment.games.security.spring.social.facebook

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder
import org.springframework.social.facebook.api.*

/**
 * Date: 12/24/14
 * Time: 2:51 PM
 */
class FacebookFriendFinderTest extends GameCoreTestCase {
    FacebookFriendFinder friendFinder = new FacebookFriendFinder()

    void testHandlesSourceWhenFacebookAvailableAndIsSourceFacebook() {
        assert !friendFinder.handlesSource("facebook")
        friendFinder.facebook = [] as Facebook
        assert friendFinder.handlesSource("facebook")
        assert !friendFinder.handlesSource(ManualPlayer.MANUAL_SOURCE)
        assert !friendFinder.handlesSource("twitter")
    }

    void testFindFriends() {
        def R1 = new Reference("1")
        def R2 = new Reference("2")
        def R3 = new Reference("4")
        def PTHREE_REF = new Reference(PTHREE.sourceId)
        def facebook = [
                friendOperations: {
                    return [
                            getFriends: {
                                return new PagedList<Reference>(
                                        [
                                                new Reference(PTWO.sourceId),
                                                new Reference(PFOUR.sourceId),
                                                PTHREE_REF,
                                                new Reference(PINACTIVE1.sourceId),
                                        ],
                                        new PagingParameters(0, 0, 0, 0),
                                        new PagingParameters(0, 0, 0, 0)
                                )
                            }
                    ] as FriendOperations
                },
                fetchConnections: {
                    String start, String type, Class ret, String[] va ->
                        assert start == "me"
                        assert type == "invitable_friends"
                        assert ret == Reference.class

                        return new PagedList<Reference>(
                                [
                                        R1,
                                        R2,
                                        R3,
                                ],
                                new PagingParameters(0, 0, 0, 0),
                                new PagingParameters(0, 0, 0, 0)
                        )
                }
        ] as Facebook
        def repo = [
                findBySourceAndSourceIdIn: {
                    String source, Collection<String> sourceIds ->
                        assert source == "facebook"
                        assert sourceIds.toSet() == [PTWO.sourceId, PTHREE.sourceId, PFOUR.sourceId, PINACTIVE1.sourceId] as Set
                        return [PTWO, PFOUR, PINACTIVE1] // leaving out PTHREE as not found
                }
        ] as AbstractPlayerRepository

        friendFinder.playerRepository = repo
        friendFinder.facebook = facebook


        def friends = friendFinder.findFriends(PONE)
        assert friends == [
                (SourceBasedFriendFinder.FRIENDS_KEY)          : [PTWO, PFOUR, PINACTIVE1] as Set,
                (SourceBasedFriendFinder.NOT_FOUND_KEY)        : [PTHREE_REF] as Set,
                (SourceBasedFriendFinder.INVITABLE_FRIENDS_KEY): [R1, R2, R3] as Set
        ]
    }
}
