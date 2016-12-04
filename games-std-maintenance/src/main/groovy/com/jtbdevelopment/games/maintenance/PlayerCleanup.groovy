package com.jtbdevelopment.games.maintenance

import com.jtbdevelopment.core.spring.social.dao.AbstractUsersConnectionRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.connect.Connection
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 8/18/15
 * Time: 10:46 PM
 *
 * TODO - perhaps we should archive them in the future and/or move them to a compressed collection
 */
@Component
@CompileStatic
class PlayerCleanup {
    private static final Logger logger = LoggerFactory.getLogger(PlayerCleanup.class)

    private static final ZoneId GMT = ZoneId.of("GMT")
    //  Don't change this number without reviewing GameCleanup
    private static final int DAYS_BACK = 90

    @Autowired
    AbstractPlayerRepository playerRepository

    @Autowired(required = false)
    AbstractUsersConnectionRepository usersConnectionRepository

    void deleteInactivePlayers() {
        def cutoff = ZonedDateTime.now(GMT).minusDays(DAYS_BACK)
        logger.info('Deleting players not logged in since ' + cutoff)

        if (usersConnectionRepository != null) {
            playerRepository.findByLastLoginLessThan(cutoff).each {
                def userSpecificRepository = usersConnectionRepository.createConnectionRepository(it.idAsString)
                if (userSpecificRepository != null) {
                    MultiValueMap<String, Connection<?>> connections = userSpecificRepository.findAllConnections()
                    connections.keySet().each {
                        String provider ->
                            List<Connection<?>> listOfConnections = connections.get(provider)
                            listOfConnections.each {
                                Connection connection ->
                                    userSpecificRepository.removeConnection(connection.key)
                            }
                    }
                }
            }
        }
        logger.info('Deleted player count = ' + playerRepository.deleteByLastLoginLessThan(cutoff))
    }
}
