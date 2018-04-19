package com.jtbdevelopment.games.security.spring.redirects

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 8/29/2015
 * Time: 3:07 PM
 */
@CompileStatic
class MobileAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    protected final
    static Logger logger = LoggerFactory.getLogger(MobileAwareFailureAuthenticationHandler.class)
    private final MobileAppChecker mobileAppChecker

    MobileAwareLoginUrlAuthenticationEntryPoint(
            final String loginFormUrl, final MobileAppChecker mobileAppChecker) {
        super(loginFormUrl)
        this.mobileAppChecker = mobileAppChecker
    }

    @Override
    void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException) throws IOException, ServletException {
        if (mobileAppChecker.isMobileRequest(request)) {
            logger.debug("Mobile flag, no failure url set, sending 401 Unauthorized error");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Required.");
        } else {
            super.commence(request, response, authException)
        }
    }
}
