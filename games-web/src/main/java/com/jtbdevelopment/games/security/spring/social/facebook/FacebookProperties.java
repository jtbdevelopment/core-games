package com.jtbdevelopment.games.security.spring.social.facebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 12/13/14 Time: 9:03 PM
 */
@Component
public class FacebookProperties {

  private static final Logger logger = LoggerFactory.getLogger(FacebookProperties.class);
  private final String clientID;
  private final String clientSecret;
  private final String permissions;
  private boolean warnings = true;

  public FacebookProperties(
      @Value("${facebook.clientID:}") final String clientID,
      @Value("${facebook.clientSecret:}") final String clientSecret,
      @Value("${facebook.permissions:public_profile,email,user_friends}") final String permissions) {
    this.clientID = clientID;
    this.clientSecret = clientSecret;
    this.permissions = permissions;
    if (StringUtils.isEmpty(clientID) || StringUtils.isEmpty(clientSecret)) {
      warnings = true;
      logger.warn(
          "----------------------------------------------------------------------------------------------");
      logger.warn(
          "----------------------------------------------------------------------------------------------");
      logger.warn(
          "----------------------------------------------------------------------------------------------");
      logger.warn(
          "facebook.clientID AND/OR facebook.clientSecret is using default values.  Not likely to work!!!");
      logger.warn(
          "----------------------------------------------------------------------------------------------");
      logger.warn(
          "----------------------------------------------------------------------------------------------");
      logger.warn(
          "----------------------------------------------------------------------------------------------");
    } else {
      warnings = false;
    }
  }

  public boolean isWarnings() {
    return warnings;
  }

  public String getClientID() {
    return clientID;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getPermissions() {
    return permissions;
  }
}
