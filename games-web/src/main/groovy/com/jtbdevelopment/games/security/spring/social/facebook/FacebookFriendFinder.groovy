package com.jtbdevelopment.games.security.spring.social.facebook

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.facebook.api.PagedList
import org.springframework.social.facebook.api.Reference
import org.springframework.stereotype.Component

/**
 * Date: 12/20/2014
 * Time: 11:17 PM
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
@CompileStatic
class FacebookFriendFinder implements SourceBasedFriendFinder {
    @Autowired
    AbstractPlayerRepository playerRepository

    //  required = false - primarily for integration tests
    @Autowired(required = false)
    Facebook facebook

    @Override
    boolean handlesSource(final String source) {
        return "facebook" == source && facebook != null
    }

    @Override
    Map<String, Set<Object>> findFriends(final Player player) {
        Map<String, Set<Object>> results = [
                (FRIENDS_KEY)          : [] as Set,
                (NOT_FOUND_KEY)        : [] as Set,
                (INVITABLE_FRIENDS_KEY): [] as Set
        ]

        PagedList<Reference> friends = facebook.friendOperations().friends
        List<String> friendSourceIds = friends.collect { Reference it -> it.id }
        List<Player> players = playerRepository.findBySourceAndSourceIdIn("facebook", friendSourceIds)
        Map<String, Player> sourceIdPlayerMap = players.collectEntries { Player p -> return [(p.sourceId): p] }
        friends.each {
            Reference it ->
                Player p = sourceIdPlayerMap[it.id]
                if (p) {
                    results[FRIENDS_KEY].add(p)
                } else {
                    results[NOT_FOUND_KEY].add(it)
                }
        }
        PagedList<Reference> canInvite = facebook.fetchConnections("me", "invitable_friends", Reference.class)
        results[INVITABLE_FRIENDS_KEY].addAll(canInvite)
        return results
    }
}
