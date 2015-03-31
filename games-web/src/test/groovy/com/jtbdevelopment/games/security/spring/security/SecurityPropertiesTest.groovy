package com.jtbdevelopment.games.security.spring.security

import org.springframework.beans.factory.annotation.Value

/**
 * Date: 1/12/15
 * Time: 6:43 PM
 */
class SecurityPropertiesTest extends GroovyTestCase {
    SecurityProperties properties = new SecurityProperties()

    void testValueAnnotations() {
        assert SecurityProperties.class.
                getDeclaredField('allowBasicAuth')?.
                getAnnotation(Value.class)?.
                value() == '${http.allowBasicAuth:false}'
    }
}
