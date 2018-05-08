package com.jtbdevelopment.games.security.spring.social.facebook.provider;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

/**
 * Based on spring social facebook FacebookServiceProvider - only change is to swap out
 * Oauth2Template
 */
public class CustomFacebookServiceProvider extends AbstractOAuth2ServiceProvider<Facebook> {

  private String appNamespace;

  CustomFacebookServiceProvider(String appId, String appSecret, String appNamespace) {
    super(getOAuth2Template(appId, appSecret));
    this.appNamespace = appNamespace;
  }

  private static OAuth2Template getOAuth2Template(String appId, String appSecret) {
    FacebookTokenExchangingOAuth2Template oAuth2Template = new FacebookTokenExchangingOAuth2Template(
        appId, appSecret, "https://www.facebook.com/v2.3/dialog/oauth",
        GraphApi.GRAPH_API_URL + "oauth/access_token");
    oAuth2Template.setUseParametersForClientAuthentication(true);
    return oAuth2Template;
  }

  public Facebook getApi(String accessToken) {
    return new FacebookTemplate(accessToken, appNamespace);
  }
}
