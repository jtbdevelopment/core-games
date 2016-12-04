package com.jtbdevelopment.games.security.spring.social.facebook.provider

import groovy.transform.CompileStatic
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.security.provider.OAuth2AuthenticationService

/**
 * Based on spring social facebook FacebookAuthenticationService
 * only change is to swap out connection factory
 * so it can swap out service provider
 * so it can swap out the oauth template - which is the real change
 */
@CompileStatic
class CustomFacebookAuthenticationService extends OAuth2AuthenticationService<Facebook> {

    CustomFacebookAuthenticationService(String apiKey, String appSecret) {
        super(new CustomFacebookConnectionFactory(apiKey, appSecret))
    }

    CustomFacebookAuthenticationService(String apiKey, String appSecret, String appNamespace) {
        super(new CustomFacebookConnectionFactory(apiKey, appSecret, appNamespace))
    }

}