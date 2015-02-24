package com.jtbdevelopment.games.rest.aop

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * Date: 2/23/15
 * Time: 7:26 PM
 */
class AspectConfigurationTest extends GroovyTestCase {
    void testAnnotations() {
        assert AspectConfiguration.class.getAnnotation(EnableAspectJAutoProxy.class)
        assert AspectConfiguration.class.getAnnotation(Configuration.class)
    }
}
