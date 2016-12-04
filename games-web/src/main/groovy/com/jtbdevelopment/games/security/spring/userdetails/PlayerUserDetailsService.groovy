package com.jtbdevelopment.games.security.spring.userdetails

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.ManualPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.security.spring.LastLoginUpdater
import com.jtbdevelopment.games.security.spring.PlayerUserDetails
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

/**
 * Date: 12/16/14
 * Time: 12:31 PM
 *
 * This is presumably only used by manual login where username = sourceId and source = MANUAL
 */
@Component
@CompileStatic
class PlayerUserDetailsService implements UserDetailsService {
    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    LastLoginUpdater lastLoginUpdater

    @Override
    UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Player player = playerRepository.findBySourceAndSourceId(ManualPlayer.MANUAL_SOURCE, username)
        if (player) {
            return new PlayerUserDetails(lastLoginUpdater.updatePlayerLastLogin(player))
        } else {
            return null
        }
    }
}
