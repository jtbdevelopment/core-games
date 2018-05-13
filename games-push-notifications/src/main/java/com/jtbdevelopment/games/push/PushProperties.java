package com.jtbdevelopment.games.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Date: 10/16/15 Time: 6:42 AM
 */
@Component
public class PushProperties {

  private static final Logger logger = LoggerFactory.getLogger(PushProperties.class);
  private final String senderID;
  private final String apiKey;
  private final boolean enabled;

  public PushProperties(
      @Value("${push.senderID:}") final String senderID,
      @Value("${push.apiKey:}") String apiKey) {
    this.senderID = senderID;
    this.apiKey = apiKey;
    if (StringUtils.isEmpty(senderID) || StringUtils.isEmpty(apiKey)) {
      enabled = false;
      logger.warn(
          "------------------------------------------------------------------------------------------");
      logger.warn(
          "------------------------------------------------------------------------------------------");
      logger.warn(
          "push.apiKey and/or push.senderID are missing and therefore push notifications are disabled");
      logger.warn(
          "------------------------------------------------------------------------------------------");
      logger.warn(
          "------------------------------------------------------------------------------------------");
    } else {
      enabled = true;
    }

  }

  public String getSenderID() {
    return senderID;
  }

  public String getApiKey() {
    return apiKey;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
