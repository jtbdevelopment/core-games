package com.jtbdevelopment.games.security.spring.redirects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 6/13/15 Time: 7:55 PM
 */
@Component
public class MobileAppProperties {

  private final String mobileSuccessUrl;
  private final String mobileFailureUrl;

  public MobileAppProperties(
      @Value("${mobile.success.url:/api/security}") final String mobileSuccessUrl,
      @Value("${mobile.failure.url:#/app/signin}") final String mobileFailureUrl) {
    this.mobileSuccessUrl = mobileSuccessUrl;
    this.mobileFailureUrl = mobileFailureUrl;
  }


  String getMobileSuccessUrl() {
    return mobileSuccessUrl;
  }

  String getMobileFailureUrl() {
    return mobileFailureUrl;
  }
}
