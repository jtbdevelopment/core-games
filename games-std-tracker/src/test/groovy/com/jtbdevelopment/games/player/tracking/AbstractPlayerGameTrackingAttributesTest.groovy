package com.jtbdevelopment.games.player.tracking

/**
 * Date: 4/9/15
 * Time: 9:26 AM
 */
class AbstractPlayerGameTrackingAttributesTest extends GroovyTestCase {
    AbstractPlayerGameTrackingAttributes attributes = new AbstractPlayerGameTrackingAttributes() {}

    void testInitializesToZero() {
        assert attributes.availablePurchasedGames == 0
        assert attributes.freeGamesUsedToday == 0
    }
}
