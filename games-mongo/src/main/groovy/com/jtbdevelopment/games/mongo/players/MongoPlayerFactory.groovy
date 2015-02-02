package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributesFactory
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerFactory
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 12/30/2014
 * Time: 7:15 PM
 */
@Component
@CompileStatic
class MongoPlayerFactory implements PlayerFactory<ObjectId> {
    @Autowired(required = false)
    GameSpecificPlayerAttributesFactory gameSpecificPlayerAttributesFactory

    @Override
    Player<ObjectId> newPlayer() {
        def player = new MongoPlayer()
        if (gameSpecificPlayerAttributesFactory) {
            player.gameSpecificPlayerAttributes = gameSpecificPlayerAttributesFactory.newPlayerAttributes()
        }
        return player
    }

    @Override
    Player<ObjectId> newManualPlayer() {
        def player = new MongoManualPlayer()
        if (gameSpecificPlayerAttributesFactory) {
            player.gameSpecificPlayerAttributes = gameSpecificPlayerAttributesFactory.newManualPlayerAttributes()
        }
        return player
    }

    @Override
    Player<ObjectId> newSystemPlayer() {
        def player = new MongoSystemPlayer()
        if (gameSpecificPlayerAttributesFactory) {
            player.gameSpecificPlayerAttributes = gameSpecificPlayerAttributesFactory.newSystemPlayerAttributes()
        }
        return player
    }
}
