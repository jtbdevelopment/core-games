package com.jtbdevelopment.games.security.spring.social.facebook;

import javax.annotation.PostConstruct;
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
  @Value("${facebook.clientID:NOTSET}")
  protected String clientID;
  @Value("${facebook.clientSecret:NOTSET}")
  protected String clientSecret;
  @Value("${facebook.permissions:public_profile,email,user_friends}")
  protected String permissions;
  private boolean warnings = true;

  @PostConstruct
  public void testDefaults() {
    if (StringUtils.isEmpty(clientID) || clientID.equals("NOTSET") || StringUtils
        .isEmpty(clientSecret) || clientSecret.equals("NOTSET")) {
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
