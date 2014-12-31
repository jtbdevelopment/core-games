package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerFactory
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 12/30/2014
 * Time: 7:15 PM
 */
@Component
@CompileStatic
class MongoPlayerFactory implements PlayerFactory<ObjectId> {
    @Override
    Player<ObjectId> newPlayer() {
        return new MongoPlayer()
    }

    @Override
    Player<ObjectId> newManualPlayer() {
        return new MongoManualPlayer()
    }

    @Override
    Player<ObjectId> newSystemPlayer() {
        return new MongoSystemPlayer()
    }
}
