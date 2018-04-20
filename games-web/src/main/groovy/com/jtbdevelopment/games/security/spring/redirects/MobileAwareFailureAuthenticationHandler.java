package com.jtbdevelopment.games.security.spring.redirects;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Date: 6/2/15 Time: 3:32 PM
 */
public class MobileAwareFailureAuthenticationHandler extends SimpleUrlAuthenticationFailureHandler {

    protected static final Logger logger = LoggerFactory
        .getLogger(MobileAwareFailureAuthenticationHandler.class);
    private final MobileAppChecker checker;

    public MobileAwareFailureAuthenticationHandler(final MobileAppChecker checker) {
        this.checker = checker;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
        final HttpServletResponse response, final AuthenticationException exception)
        throws IOException, ServletException {
        if (checker.isMobileRequest(request)) {
            logger.debug("Mobile flag, no failure url set, sending 401 Unauthorized error");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication Failed: " + exception.getMessage());
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }

    }
}
