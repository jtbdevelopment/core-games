package com.jtbdevelopment.games.security.spring.security.facebook

import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.util.StringUtils

import javax.servlet.http.HttpServletRequest
import java.util.regex.Pattern

/**
 * Date: 1/27/2015
 * Time: 1:35 PM
 */
class FacebookCanvasAllowingProtectionMatcher implements RequestMatcher {
    private Pattern allowedMethods = Pattern.compile('^(GET|HEAD|TRACE|OPTIONS)$')

    /* (non-Javadoc)
     * @see org.springframework.security.web.util.matcher.RequestMatcher#matches(javax.servlet.http.HttpServletRequest)
     */

    public boolean matches(HttpServletRequest request) {
        return !(allowedMethods.matcher(request.getMethod()).matches() ||
                (request.getMethod() == 'POST' && StringUtils.isEmpty(request.pathInfo)))
    }
}


