package com.jtbdevelopment.games.push

import org.springframework.beans.factory.annotation.Value

/**
 * Date: 10/16/15
 * Time: 6:59 PM
 */
class PushPropertiesTest extends GroovyTestCase {
    void testValueAnnotations() {
        assert PushProperties.class.
                getDeclaredField('senderID')?.
                getAnnotation(Value.class)?.
                value() == '${push.senderID:NOTSET}'
    }
}
