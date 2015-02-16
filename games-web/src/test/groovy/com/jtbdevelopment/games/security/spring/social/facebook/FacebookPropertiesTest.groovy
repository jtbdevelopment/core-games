package com.jtbdevelopment.games.security.spring.social.facebook

import org.springframework.beans.factory.annotation.Value

import javax.annotation.PostConstruct
import java.lang.reflect.Method

/**
 * Date: 1/7/15
 * Time: 7:39 PM
 */
class FacebookPropertiesTest extends GroovyTestCase {
    FacebookProperties properties = new FacebookProperties()

    void testPostConstructAnnotation() {
        Method m = properties.class.getMethod('testDefaults')
        assert m.getAnnotation(PostConstruct.class)
    }

    void testValueAnnotations() {
        assert FacebookProperties.class.
                getDeclaredField('clientID')?.
                getAnnotation(Value.class)?.
                value() == '${facebook.clientID:NOTSET}'
        assert FacebookProperties.class.
                getDeclaredField('clientSecret')?.
                getAnnotation(Value.class)?.
                value() == '${facebook.clientSecret:NOTSET}'
        assert FacebookProperties.class.
                getDeclaredField('permissions')?.
                getAnnotation(Value.class)?.
                value() == '${facebook.permissions:public_profile,email,user_friends}'
    }

    void testGeneratesWarningOnBothNull() {
        properties.clientSecret = null
        properties.clientID = null
        properties.testDefaults()
        assert properties.warnings
    }

    void testGeneratesWarningOnBothBlank() {
        properties.clientSecret = ''
        properties.clientID = ''
        properties.testDefaults()
        assert properties.warnings
    }

    void testGeneratesWarningOnBoth() {
        properties.clientSecret = 'NOTSET'
        properties.clientID = 'NOTSET'
        properties.testDefaults()
        assert properties.warnings
    }

    void testGeneratesWarningOnSecret() {
        properties.clientSecret = 'NOTSET'
        properties.clientID = 'SET'
        properties.testDefaults()
        assert properties.warnings
    }

    void testGeneratesWarningOnAppID() {
        properties.clientSecret = 'SET'
        properties.clientID = 'NOTSET'
        properties.testDefaults()
        assert properties.warnings
    }

    void testNoWarningWhenPropertiesSet() {
        properties.clientSecret = 'SET'
        properties.clientID = 'SET'
        properties.testDefaults()
        assertFalse properties.warnings
    }
}
