package com.jtbdevelopment.games.security.spring.social.facebook.provider;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Date: 9/16/2015 Time: 10:58 AM
 *
 * TODO - should we add a debug token call to prevent token hijacking?
 * https://developers.facebook.com/docs/facebook-login/access-tokens#debug
 */
public class FacebookTokenExchangingOAuth2Template extends OAuth2Template {

  private String clientId;
  private String clientSecret;
  private String accessTokenUrl;

  public FacebookTokenExchangingOAuth2Template(final String clientId, final String clientSecret,
      final String authorizeUrl, final String accessTokenUrl) {
    super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.accessTokenUrl = accessTokenUrl;
  }

  @Override
  public AccessGrant exchangeForAccess(final String authorizationCode, final String redirectUri,
      final MultiValueMap<String, String> additionalParameters) {
    try {
      return super.exchangeForAccess(authorizationCode, redirectUri, additionalParameters);
    } catch (Exception ignored) {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
      params.set("client_id", clientId);
      params.set("client_secret", clientSecret);
      params.set("fb_exchange_token", authorizationCode);
      params.set("redirect_uri", redirectUri);
      params.set("grant_type", "fb_exchange_token");
      if (additionalParameters != null) {
        params.putAll(additionalParameters);
      }

      return postForAccessGrant(accessTokenUrl, params);
    }

  }
}
