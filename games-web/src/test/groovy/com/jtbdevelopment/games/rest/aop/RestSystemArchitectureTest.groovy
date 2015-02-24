package com.jtbdevelopment.games.rest.aop

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

import java.lang.reflect.Method

/**
 * Date: 12/22/2014
 * Time: 10:30 PM
 */
class RestSystemArchitectureTest extends GroovyTestCase {
    void testClassAnnotationIsPresent() {
        assert RestSystemArchitecture.class.getAnnotation(Aspect.class) != null
    }

    void testRestServicesPointCutAnnotation() {
        Method m = RestSystemArchitecture.class.getMethod("inRestServices", [] as Class<?>[])
        Pointcut p = m.getAnnotation(Pointcut.class)
        assert p != null
        assert p.value() == "within(com.jtbdevelopment..*.rest.services..*)"
    }
}
