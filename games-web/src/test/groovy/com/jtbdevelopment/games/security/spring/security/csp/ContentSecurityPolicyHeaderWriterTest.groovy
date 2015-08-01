package com.jtbdevelopment.games.security.spring.security.csp

import org.springframework.mock.web.MockHttpServletResponse

/**
 * Date: 7/31/15
 * Time: 10:30 PM
 */
class ContentSecurityPolicyHeaderWriterTest extends GroovyTestCase {
    public static final String CSP = 'Content-Security-Policy'
    private ContentSecurityPolicyHeaderWriter writer = new ContentSecurityPolicyHeaderWriter()

    void testWriteHeaders() {
        MockHttpServletResponse response = new MockHttpServletResponse()

        writer.writeHeaders(null, response)

        assert response.containsHeader(CSP)
        assert response.getHeader(CSP) == "frame-ancestors 'self' *.facebook.com ;"
    }
}
