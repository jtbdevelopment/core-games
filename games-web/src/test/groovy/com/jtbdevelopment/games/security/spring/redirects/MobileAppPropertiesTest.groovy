package com.jtbdevelopment.games.security.spring.redirects

import org.springframework.beans.factory.annotation.Value

/**
 * Date: 6/14/15
 * Time: 7:28 AM
 */
class MobileAppPropertiesTest extends GroovyTestCase {
    void testValueAnnotations() {
        assert MobileAppProperties.class.
                getDeclaredField('mobileSuccessUrl')?.
                getAnnotation(Value.class)?.
                value() == '${mobile.success.url:/api/security}'
        assert MobileAppProperties.class.
                getDeclaredField('mobileFailureUrl')?.
                getAnnotation(Value.class)?.
                value() == '${mobile.failure.url:#/app/signin}'
    }

}
