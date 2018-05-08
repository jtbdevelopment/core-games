package com.jtbdevelopment.games.security.spring.redirects;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Date: 6/13/15 Time: 7:37 PM
 */
@Component
public class MobileAppChecker {

  boolean isMobileRequest(final HttpServletRequest request) {
    String origin = request != null ? request.getHeader("Origin") : null;
    return origin != null && origin.startsWith("file:");
  }

}
