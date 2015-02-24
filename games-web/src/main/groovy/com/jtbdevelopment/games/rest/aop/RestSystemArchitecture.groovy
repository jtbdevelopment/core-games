package com.jtbdevelopment.games.rest.aop

import groovy.transform.CompileStatic
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

/**
 * Date: 11/12/14
 * Time: 7:06 PM
 */
@Aspect
@CompileStatic
@Component
class RestSystemArchitecture {
    @SuppressWarnings("GroovyUnusedDeclaration")
    @Pointcut("within(com.jtbdevelopment..*.rest.services..*)")
    public void inRestServices() {}
}
