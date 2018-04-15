package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.StringPlayer
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerMasker
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

/**
 * Date: 11/26/14
 * Time: 4:18 PM
 */
class FriendFinderTest extends GameCoreTestCase {
    FriendFinder finder

    void testClassAnnotations() {
        Scope scope = FriendFinder.class.getAnnotation(Scope.class)
        assert scope
        assert scope.proxyMode() == ScopedProxyMode.INTERFACES
        assert scope.value() == ConfigurableBeanFactory.SCOPE_PROTOTYPE
    }

    void testSumOfSourceBasedFindersV2Masking() {
        def f1 = [
                handlesSource: {
                    String it ->
                        true
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
                        true
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
                        false
                }
        ] as SourceBasedFriendFinder

        def repository = [
                findById: {
                    String it ->
                        assert it == PFOUR.id
                        return Optional.of(PFOUR)
                }
        ] as AbstractPlayerRepository<String, StringPlayer>
        def masked = [['md5': 'x', 'displayName': 'y'], ['id': '1', 'displayName': '2']]
        def masker = [
                maskFriendsV2: {
                    Set<Player> friends ->
                        assert friends == [PONE, PTWO, PTHREE] as Set
                        return masked
                }
        ] as PlayerMasker
        finder = new FriendFinder(repository, [f1, f2, f3], masker)

        def results = finder.findFriendsV2(PFOUR.id)
        assert 3 == results.size()
        assert ['Yes'] as Set == results.get('Y')
        assert [1, 2, 3] as Set == results.get('X')
        assert masked as Set == results.get(SourceBasedFriendFinder.MASKED_FRIENDS_KEY)
    }

    void testEmptyMaskedFriendsIfNoFriendsV2() {
        def f1 = [
                handlesSource: {
                    String it ->
                        true
                },
                findFriends  : {
                    StringPlayer p ->
                        assert p.is(PFOUR)
                        return [:]
                }
        ] as SourceBasedFriendFinder

        def repository = [
                findById: {
                    String it ->
                        assert it == PFOUR.id
                        return Optional.of(PFOUR)
                }
        ] as AbstractPlayerRepository<String, StringPlayer>
        finder = new FriendFinder(repository, [f1], null)

        assert finder.findFriendsV2(PFOUR.id) == [
                (SourceBasedFriendFinder.MASKED_FRIENDS_KEY): [] as Set
        ]
    }

    void testNoPlayerInRepositoryV2() {
        def repository = [
                findById: {
                    String it ->
                        assert it == PFOUR.id
                        return Optional.empty()
                }
        ] as AbstractPlayerRepository<String, StringPlayer>
        finder = new FriendFinder(repository, [], null)

        try {
            finder.findFriendsV2(PFOUR.id)
            fail("should have failed")
        } catch (FailedToFindPlayersException e) {
            //
        }
    }

    void testDisabledPlayerV2() {
        def repository = [
                findById: {
                    String it ->
                        assert it == PINACTIVE1.id
                        return Optional.of(PINACTIVE1)
                }
        ] as AbstractPlayerRepository<String, StringPlayer>
        finder = new FriendFinder(repository, [], null)

        try {
            finder.findFriendsV2(PINACTIVE1.id)
            fail("should have failed")
        } catch (FailedToFindPlayersException e) {
            //
        }
    }
}
