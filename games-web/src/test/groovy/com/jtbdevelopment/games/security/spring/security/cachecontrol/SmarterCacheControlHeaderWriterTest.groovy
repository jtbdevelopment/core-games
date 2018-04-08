package com.jtbdevelopment.games.security.spring.security.cachecontrol

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import java.time.Instant

/**
 * Date: 6/8/15
 * Time: 7:03 AM
 */
class SmarterCacheControlHeaderWriterTest extends GroovyTestCase {
    public static final String EXPIRES = "Expires"
    public static final String PRAGMA = "Pragma"
    public static final String CACHE_CONTROL = "Cache-Control"
    SmarterCacheControlHeaderWriter writer = new SmarterCacheControlHeaderWriter()

    void testWriteHeadersWithCache() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        request.servletPath = "/images/animage.png"

        Instant nowPlus1Hour = Instant.now().plusSeconds(60 * 60)
        Instant plus30seconds = nowPlus1Hour.plusSeconds(30)

        writer.writeHeaders(request, response)

        assert response.containsHeader(EXPIRES)
        assert Long.parseLong(response.getHeader(EXPIRES)) >= nowPlus1Hour.epochSecond
        assert Long.parseLong(response.getHeader(EXPIRES)) <= plus30seconds.epochSecond
        assertFalse response.containsHeader(PRAGMA)
        assertFalse response.containsHeader(CACHE_CONTROL)
    }

    void testWriteHeadersAllowsCachingFor() {
        ["/images", "/scripts", "/styles", "/bower_components", '/views'].each {
            MockHttpServletRequest request = new MockHttpServletRequest()
            MockHttpServletResponse response = new MockHttpServletResponse()

            request.servletPath = it + "X"

            writer.writeHeaders(request, response)

            assert response.containsHeader(EXPIRES)
            assertFalse "0" == response.getHeader(EXPIRES)
            assertFalse response.containsHeader(PRAGMA)
            assertFalse response.containsHeader(CACHE_CONTROL)
        }
    }

    void testWriteHeadersWithNoCache() {
        MockHttpServletRequest request = new MockHttpServletRequest()
        MockHttpServletResponse response = new MockHttpServletResponse()
        request.servletPath = "/other/animage.png"

        writer.writeHeaders(request, response)

        assert response.containsHeader(EXPIRES)
        assert response.containsHeader(PRAGMA)
        assert response.containsHeader(CACHE_CONTROL)
        assert "0" == response.getHeader(EXPIRES)
        assert "no-cache" == response.getHeader(PRAGMA)
        assert "no-cache, no-store, max-age=0, must-revalidate" == response.getHeader(CACHE_CONTROL)
    }
}
