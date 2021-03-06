package com.jtbdevelopment.games.security.spring.redirects;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Date: 8/29/2015 Time: 3:07 PM
 */
public class MobileAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  protected static final Logger logger = LoggerFactory
      .getLogger(MobileAwareFailureAuthenticationHandler.class);
  private final MobileAppChecker mobileAppChecker;

  public MobileAwareLoginUrlAuthenticationEntryPoint(final String loginFormUrl,
      final MobileAppChecker mobileAppChecker) {
    super(loginFormUrl);
    this.mobileAppChecker = mobileAppChecker;
  }

  @Override
  public void commence(final HttpServletRequest request, final HttpServletResponse response,
      final AuthenticationException authException) throws IOException, ServletException {
    if (mobileAppChecker.isMobileRequest(request)) {
      logger.debug("Mobile flag, no failure url set, sending 401 Unauthorized error");

      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Required.");
    } else {
      super.commence(request, response, authException);
    }

  }
}
