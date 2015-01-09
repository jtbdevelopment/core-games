package com.jtbdevelopment.games.mongo.players.friendfinder

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

/**
 * Date: 1/8/15
 * Time: 10:14 PM
 */
class FriendFinderTest extends GroovyTestCase {
    FriendFinder finder = new FriendFinder()

    void testClassAnnotations() {
        assert FriendFinder.class.getAnnotation(Component.class)
        Scope scope = FriendFinder.class.getAnnotation(Scope.class)
        assert scope
        assert scope.proxyMode() == ScopedProxyMode.INTERFACES
        assert scope.value() == ConfigurableBeanFactory.SCOPE_PROTOTYPE

    }
}
