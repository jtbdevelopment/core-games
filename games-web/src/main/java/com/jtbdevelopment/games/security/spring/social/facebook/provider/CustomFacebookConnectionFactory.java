package com.jtbdevelopment.games.security.spring.social.facebook.provider;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookAdapter;

/**
 * Based on spring social facebook FacebookConnectionFactory swap out service provider so it can
 * swap out the oauth template - which is the real change
 */
public class CustomFacebookConnectionFactory extends OAuth2ConnectionFactory<Facebook> {

  CustomFacebookConnectionFactory(String appId, String appSecret) {
    this(appId, appSecret, null);
  }

  CustomFacebookConnectionFactory(String appId, String appSecret, String appNamespace) {
    super("facebook",
        new CustomFacebookServiceProvider(appId, appSecret, appNamespace),
        new FacebookAdapter());
  }
}
