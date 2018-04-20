package com.jtbdevelopment.games.security.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 12/20/2014 Time: 4:48 PM
 */
@Component
public class SecurityProperties {

  @Value("${http.allowBasicAuth:false}")
  private String allowBasicAuth;

  public String getAllowBasicAuth() {
    return allowBasicAuth;
  }

  public void setAllowBasicAuth(String allowBasicAuth) {
    this.allowBasicAuth = allowBasicAuth;
  }
}
