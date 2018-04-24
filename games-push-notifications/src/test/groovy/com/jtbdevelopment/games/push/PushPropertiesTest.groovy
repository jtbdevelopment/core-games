package com.jtbdevelopment.games.push

import org.springframework.beans.factory.annotation.Value

import javax.annotation.PostConstruct
import java.lang.reflect.Method

/**
 * Date: 10/16/15
 * Time: 6:59 PM
 */
class PushPropertiesTest extends GroovyTestCase {
    PushProperties properties = new PushProperties()

    void testPostConstructAnnotation() {
        Method m = properties.class.getMethod('testSetup')
        assert m.getAnnotation(PostConstruct.class)
    }

    void testValueAnnotations() {
        assert PushProperties.class.
                getDeclaredField('senderID')?.
                getAnnotation(Value.class)?.
                value() == '${push.senderID:}'
    }

    void testEnabledFlagSetWithBothValuesSet() {
        properties.senderID = "1234"
        properties.apiKey = "3z35df"
        properties.testSetup()
        assert properties.enabled
    }

    void testEnabledFlagSetWithNoAPIKey() {
        properties.senderID = "1234"
        properties.apiKey = ""
        properties.testSetup()
        assertFalse properties.enabled
    }

    void testEnabledFlagSetWithNoSenderID() {
        properties.senderID = ""
        properties.apiKey = "3z35df"
        properties.testSetup()
        assertFalse properties.enabled
    }
}
