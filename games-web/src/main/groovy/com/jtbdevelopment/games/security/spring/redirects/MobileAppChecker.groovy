package com.jtbdevelopment.games.security.spring.redirects

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

/**
 * Date: 6/13/15
 * Time: 7:37 PM
 */
@Component
@CompileStatic
class MobileAppChecker {
    @Value('${mobile.cookie.name:mobile.jtbdevelopment}')
    String mobileCookie

    boolean isMobileRequest(final HttpServletRequest request) {
        Cookie cookie = request?.cookies?.find {
            Cookie cookie ->
                cookie.name == mobileCookie

        }
        return cookie != null && Boolean.parseBoolean(cookie.value) == Boolean.TRUE
    }
}
