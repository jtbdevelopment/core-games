package com.jtbdevelopment.games.mongo.players

import com.jtbdevelopment.games.players.ManualPlayer
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 12/16/14
 * Time: 6:49 AM
 *
 * For direct login users
 *
 * Username goes in sourceId which should be an email address
 * displayName might default to display name
 *
 */
@Document(collection = "player")
@CompileStatic
class MongoManualPlayer extends MongoPlayer implements ManualPlayer<ObjectId> {
    String password

    boolean verified = false
    //  TODO
    String verificationToken = ""

    public MongoManualPlayer() {
        super.source = MANUAL_SOURCE
    }
}
