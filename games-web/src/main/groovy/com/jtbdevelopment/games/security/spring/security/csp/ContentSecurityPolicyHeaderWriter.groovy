package com.jtbdevelopment.games.security.spring.security.csp

import org.springframework.security.web.header.HeaderWriter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 7/31/15
 * Time: 10:17 PM
 */
class ContentSecurityPolicyHeaderWriter implements HeaderWriter {
    @Override
    void writeHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        //  TODO - validate sources as well
        response.addHeader('Content-Security-Policy', "frame-ancestors 'self' *.facebook.com ;")
    }
}
