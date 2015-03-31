package com.jtbdevelopment.games.security.spring.security.facebook
/**
 * Date: 1/27/2015
 * Time: 3:44 PM
 */
class FacebookCanvasXFrameAllowFromStrategyTest extends GroovyTestCase {
    FacebookCanvasXFrameAllowFromStrategy allowFromStrategy = new FacebookCanvasXFrameAllowFromStrategy()

    void testGetAllowFromValue() {
        assert allowFromStrategy.getAllowFromValue(null) == "https://apps.facebook.com"
    }
}
