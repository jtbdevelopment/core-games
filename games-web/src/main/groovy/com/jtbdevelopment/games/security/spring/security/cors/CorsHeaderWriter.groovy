package com.jtbdevelopment.games.security.spring.security.cors

import org.springframework.security.web.header.HeaderWriter
import org.springframework.util.StringUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 8/26/15
 * Time: 6:39 PM
 *
 * Primarily for testing locally and should not normally be used
 *
 * No tests for same reason
 */
class CorsHeaderWriter implements HeaderWriter {

    @Override
    void writeHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        String origin = request.getHeader('Origin')
        if (origin && !origin.startsWith('file:')) {
            response.addHeader('Access-Control-Allow-Origin', origin)
            response.addHeader('Access-Control-Allow-Credentials', 'true')
            response.addHeader('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS')
            response.addHeader('Access-Control-Max-Age', '3600')

            def allowedHeaders = request.getHeader('Access-Control-Request-Headers')
            if (!StringUtils.isEmpty(allowedHeaders)) {
                response.addHeader('Access-Control-Allow-Headers', allowedHeaders)
            }
        }
    }
}

