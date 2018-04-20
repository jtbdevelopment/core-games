package com.jtbdevelopment.games.security.spring.redirects;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationRedirectException;

/**
 * Date: 6/12/15 Time: 10:01 PM
 */
public class MobileAwareSocialFailureAuthenticationHandler extends
    SocialAuthenticationFailureHandler {

    protected static final Logger logger = LoggerFactory
        .getLogger(MobileAwareSocialFailureAuthenticationHandler.class);
    private final MobileAppChecker checker;
    private final MobileAppProperties mobileAppProperties;

    public MobileAwareSocialFailureAuthenticationHandler(final MobileAppChecker checker,
        final MobileAppProperties mobileAppProperties) {
        super(new SimpleUrlAuthenticationFailureHandler());
        this.mobileAppProperties = mobileAppProperties;
        this.checker = checker;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
        final HttpServletResponse response, final AuthenticationException failed)
        throws IOException, ServletException {
        if (checker.isMobileRequest(request)
            && !(failed instanceof SocialAuthenticationRedirectException)) {
            logger.info("Mobile social failure to login - calculating default mobile app url");
            String url = request.getHeader("Referer") + mobileAppProperties.getMobileFailureUrl();
            logger.debug(url);

            response.sendRedirect(url);
        } else {
            super.onAuthenticationFailure(request, response, failed);
        }

    }
}
