package com.jtbdevelopment.games.security.spring.social.facebook;

import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;

/**
 * Date: 12/16/14 Time: 12:56 PM
 */
@Configuration
public class FacebookConfig {

  @Bean
  @Autowired
  public CustomFacebookAuthenticationService facebookAuthenticationService(
      final FacebookProperties facebookProperties
  ) {
    CustomFacebookAuthenticationService service = new CustomFacebookAuthenticationService(
        facebookProperties.getClientID(), facebookProperties.getClientSecret());
    service.setDefaultScope(facebookProperties.getPermissions());
    return service;
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
  @Autowired
  public Facebook facebook(final ConnectionRepository connectionRepository) {
    Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);
    return (connection == null ? null : connection.getApi());
  }

}
