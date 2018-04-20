package com.jtbdevelopment.games.security.spring.security.facebook;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

/**
 * Date: 1/27/2015 Time: 1:35 PM
 */
public class FacebookCanvasAllowingProtectionMatcher implements RequestMatcher {

  private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

  public boolean matches(HttpServletRequest request) {
    return !(allowedMethods.matcher(request.getMethod()).matches() || (
        request.getMethod().equals("POST") && StringUtils.isEmpty(request.getPathInfo())));
  }
}
