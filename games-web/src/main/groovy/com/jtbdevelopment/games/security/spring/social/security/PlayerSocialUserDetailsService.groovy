package com.jtbdevelopment.games.security.spring.social.security

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.dao.StringToIDConverter
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.security.spring.LastLoginUpdater
import com.jtbdevelopment.games.security.spring.PlayerUserDetails
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.social.security.SocialUserDetails
import org.springframework.social.security.SocialUserDetailsService
import org.springframework.stereotype.Component

/**
 * Date: 12/13/14
 * Time: 9:36 PM
 *
 */
@Component
@CompileStatic
class PlayerSocialUserDetailsService implements SocialUserDetailsService {
    @Autowired
    AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository

    @Autowired
    StringToIDConverter<? extends Serializable> stringToIDConverter

    @Autowired
    LastLoginUpdater lastLoginUpdater

    @Override
    SocialUserDetails loadUserByUserId(
            final String userId) throws UsernameNotFoundException, DataAccessException {
        def optional = playerRepository.findById(stringToIDConverter.convert(userId))
        if (optional.present) {
            return new PlayerUserDetails(lastLoginUpdater.updatePlayerLastLogin(optional.get()))
        }
        return null
    }
}
