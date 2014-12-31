package com.jtbdevelopment.games.mongo.players.friendfinder

import com.jtbdevelopment.games.players.friendfinder.AbstractFriendFinder
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
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
class FriendFinder extends AbstractFriendFinder<ObjectId> {
}
