package com.jtbdevelopment.games.player.tracking.reset

import com.jtbdevelopment.games.dao.caching.CacheConstants
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.player.tracking.AbstractPlayerGameTrackingAttributes
import com.jtbdevelopment.games.player.tracking.PlayerGameLimits
import com.jtbdevelopment.games.publish.PlayerPublisher
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

/**
 * Date: 2/11/15
 * Time: 7:10 PM
 */
@Component
@CompileStatic
class PlayerFreeGameReset {
    private static final Logger logger = LoggerFactory.getLogger(PlayerFreeGameReset.class)
    @Autowired
    MongoOperations mongoOperations

    @Autowired
    PlayerPublisher playerPublisher

    @Autowired
    PlayerGameLimits playerGameLimits

    @Caching(
            evict = [
                    @CacheEvict(value = CacheConstants.PLAYER_ID_CACHE, allEntries = true),
                    @CacheEvict(value = CacheConstants.PLAYER_MD5_CACHE, allEntries = true),
                    @CacheEvict(value = CacheConstants.PLAYER_S_AND_SID_CACHE, allEntries = true)
            ]
    )
    boolean resetFreeGames() {
        logger.info('Resetting all player free games.')
        //  Error check?
        mongoOperations.updateMulti(
                Query.query(Criteria.where(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD).gt(0)),
                Update.update(AbstractPlayerGameTrackingAttributes.FREE_GAMES_FIELD, 0),
                MongoPlayer.class
        )
        playerPublisher.publishAll()
    }
}
