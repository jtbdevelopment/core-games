package com.jtbdevelopment.games.players.friendfinder

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerMasker
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

/**
 * Date: 11/26/14
 * Time: 1:04 PM
 */
@CompileStatic
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
class FriendFinder<ID extends Serializable> {
    @Autowired
    List<SourceBasedFriendFinder> friendFinders
    @Autowired
    AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository
    @Autowired
    PlayerMasker friendMasker

    Map<String, Object> findFriendsV2(final ID playerId) {
        def optionalPlayer = playerRepository.findById(playerId)
        if (!optionalPlayer.present || optionalPlayer.get().disabled) {
            throw new FailedToFindPlayersException()
        }
        Player player = optionalPlayer.get()
        Map<String, Object> friends = [:]
        friendFinders.each {
            SourceBasedFriendFinder friendFinder ->
                if (friendFinder.handlesSource(player.source)) {
                    Map<String, Set<Object>> subFriends = friendFinder.findFriends(player)
                    subFriends.each {
                        String key, Set<Object> values ->
                            if (friends.containsKey(key)) {
                                ((Set<Object>) friends[key]).addAll(values)
                            } else {
                                friends[key] = values
                            }
                    }
                }
        }
        Set<Player> playerFriends = (Set<Player>) friends.remove(SourceBasedFriendFinder.FRIENDS_KEY)
        if (playerFriends) {
            friends[SourceBasedFriendFinder.MASKED_FRIENDS_KEY] = friendMasker.maskFriendsV2(playerFriends)
        } else {
            friends[SourceBasedFriendFinder.MASKED_FRIENDS_KEY] = []
        }
        return friends
    }
}
