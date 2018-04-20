package com.jtbdevelopment.games.security.spring.security.csp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.header.HeaderWriter;

/**
 * Date: 7/31/15 Time: 10:17 PM
 */
public class ContentSecurityPolicyHeaderWriter implements HeaderWriter {

    @Override
    public void writeHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        //  TODO - validate sources as well
        response.addHeader("Content-Security-Policy", "frame-ancestors 'self' *.facebook.com ;");
    }

}
