package com.jtbdevelopment.games.security.spring.redirects

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

/**
 * Date: 6/13/15
 * Time: 7:37 PM
 */
@Component
@CompileStatic
class MobileAppChecker {
    boolean isMobileRequest(final HttpServletRequest request) {
        String origin = request?.getHeader('Origin');
        return origin != null && origin.startsWith('file:')
    }
}
