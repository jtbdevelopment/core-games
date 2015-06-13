package com.jtbdevelopment.games.security.spring.security.cachecontrol

import groovy.transform.CompileStatic
import org.springframework.security.web.header.HeaderWriter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.ZonedDateTime

/**
 * Date: 6/8/15
 * Time: 6:38 AM
 */
@CompileStatic
class SmarterCacheControlHeaderWriter implements HeaderWriter {

    public static final String EXPIRES = "Expires"
    public static final String CACHE_CONTROL = "Cache-Control"
    public static final String PRAGMA = "Pragma"

    @Override
    void writeHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        if (allowCaching(request)) {
            //  TODO - review
            //response.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
            response.addHeader(EXPIRES, "" + ZonedDateTime.now().plusHours(1).toInstant().epochSecond)
        } else {
            response.addHeader(CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate")
            response.addHeader(PRAGMA, "no-cache")
            response.addHeader(EXPIRES, "0")
        }
    }

    private boolean allowCaching(final HttpServletRequest request) {
        String path = request.servletPath
        return path.startsWith("/images") ||
                path.startsWith("/scripts") ||
                path.startsWith("/styles") ||
                path.startsWith("/views") ||
                path.startsWith("/bower_components")
    }
}
