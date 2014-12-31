package com.jtbdevelopment.games.mongo.players.friendfinder

import com.jtbdevelopment.games.players.friendfinder.AbstractManualFriendFinder
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 12/30/14
 * Time: 9:19 AM
 */
@Component
class ManualFriendFinder extends AbstractManualFriendFinder<ObjectId> {
}
