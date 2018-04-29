package com.jtbdevelopment.games.security.spring.social.facebook;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 1/7/15 Time: 7:39 PM
 */
public class FacebookPropertiesTest {

    @Test
    public void testGeneratesWarningOnBothNull() {
        FacebookProperties properties = new FacebookProperties(null, null, "x, y, z");
        Assert.assertTrue(properties.isWarnings());
    }

    @Test
    public void testGeneratesWarningOnBothBlank() {
        FacebookProperties properties = new FacebookProperties("", "", "x, y, z");
        Assert.assertTrue(properties.isWarnings());
    }

    @Test
    public void testGeneratesWarningOnSecret() {
        FacebookProperties properties = new FacebookProperties("", "SET", "x, y, z");
        Assert.assertTrue(properties.isWarnings());
    }

    @Test
    public void testGeneratesWarningOnAppID() {
        FacebookProperties properties = new FacebookProperties("SET", "", "x, y, z");
        Assert.assertTrue(properties.isWarnings());
    }

    @Test
    public void testNoWarningWhenPropertiesSet() {
        FacebookProperties properties = new FacebookProperties("SET", "SET", "x, y, z");
        Assert.assertFalse(properties.isWarnings());
    }

}
