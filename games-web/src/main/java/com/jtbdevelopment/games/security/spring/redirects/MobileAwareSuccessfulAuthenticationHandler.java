package com.jtbdevelopment.games.security.spring.redirects;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Date: 6/2/15 Time: 6:44 AM
 */
public class MobileAwareSuccessfulAuthenticationHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    protected static final Logger logger = LoggerFactory
        .getLogger(MobileAwareSuccessfulAuthenticationHandler.class);
    private final MobileAppChecker checker;
    private final MobileAppProperties mobileAppProperties;

    public MobileAwareSuccessfulAuthenticationHandler(final MobileAppChecker checker,
        final MobileAppProperties mobileAppProperties, final String nonMobileDefaultUrl,
        final boolean nonMobileAlwaysUseTarget) {
        this.checker = checker;
        this.mobileAppProperties = mobileAppProperties;
        this.setDefaultTargetUrl(nonMobileDefaultUrl);
        this.setAlwaysUseDefaultTargetUrl(nonMobileAlwaysUseTarget);
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
        final HttpServletResponse response, final Authentication authentication)
        throws ServletException, IOException {
        if (checker.isMobileRequest(request)) {
            response.sendRedirect(mobileAppProperties.getMobileSuccessUrl());
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
