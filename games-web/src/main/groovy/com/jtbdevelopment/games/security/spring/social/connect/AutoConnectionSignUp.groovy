package com.jtbdevelopment.games.security.spring.social.connect

import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerFactory
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionSignUp
import org.springframework.stereotype.Component

/**
 * Date: 12/14/14
 * Time: 5:11 PM
 */
@Component
@CompileStatic
class AutoConnectionSignUp implements ConnectionSignUp {
    private final static Logger logger = LoggerFactory.getLogger(AutoConnectionSignUp.class)

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired
    PlayerFactory playerFactory

    @Override
    String execute(final Connection<?> connection) {
        Player player = playerRepository.findBySourceAndSourceId(connection.key.providerId, connection.key.providerUserId)
        if (player) {
            return player.id
        } else {
            Player p = playerFactory.newPlayer()
            p.disabled = false;
            p.displayName = connection.fetchUserProfile().name
            p.source = connection.key.providerId
            p.sourceId = connection.key.providerUserId
            p.profileUrl = connection.profileUrl
            p.imageUrl = connection.imageUrl
            p = playerRepository.save(p);
            return p.idAsString
        }
    }
}
