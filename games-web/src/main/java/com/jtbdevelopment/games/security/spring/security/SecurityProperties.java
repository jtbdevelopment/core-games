package com.jtbdevelopment.games.security.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 12/20/2014 Time: 4:48 PM
 */
@Component
public class SecurityProperties {

  private final String allowBasicAuth;

  public SecurityProperties(
      @Value("${http.allowBasicAuth:false}") final String allowBasicAuth
  ) {
    this.allowBasicAuth = allowBasicAuth;
  }

  public String getAllowBasicAuth() {
    return allowBasicAuth;
  }

}
