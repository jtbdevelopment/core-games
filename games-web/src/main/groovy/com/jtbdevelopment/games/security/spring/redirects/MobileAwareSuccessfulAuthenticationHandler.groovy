package com.jtbdevelopment.games.security.spring.redirects

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 6/2/15
 * Time: 6:44 AM
 */
@CompileStatic
class MobileAwareSuccessfulAuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    protected final
    static Logger logger = LoggerFactory.getLogger(MobileAwareSuccessfulAuthenticationHandler.class)

    private final MobileAppChecker checker
    private final MobileAppProperties mobileAppProperties

    MobileAwareSuccessfulAuthenticationHandler(final MobileAppChecker checker,
                                               final MobileAppProperties mobileAppProperties,
                                               final String nonMobileDefaultUrl,
                                               final boolean nonMobileAlwaysUseTarget) {
        this.checker = checker
        this.mobileAppProperties = mobileAppProperties
        this.defaultTargetUrl = nonMobileDefaultUrl
        this.alwaysUseDefaultTargetUrl = nonMobileAlwaysUseTarget
    }

    @Override
    void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws ServletException, IOException {
        if (checker.isMobileRequest(request)) {
            response.sendRedirect(mobileAppProperties.mobileSuccessUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication)
        }
    }
}
