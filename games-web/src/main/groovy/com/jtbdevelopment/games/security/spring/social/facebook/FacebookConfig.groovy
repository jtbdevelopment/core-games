package com.jtbdevelopment.games.security.spring.social.facebook

import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookAuthenticationService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.facebook.api.Facebook

/**
 * Date: 12/16/14
 * Time: 12:56 PM
 */
@Configuration
@CompileStatic
class FacebookConfig {
    @Bean
    @Autowired
    CustomFacebookAuthenticationService facebookAuthenticationService(
            final FacebookProperties facebookProperties) {
        def service = new CustomFacebookAuthenticationService(facebookProperties.getClientID(), facebookProperties.getClientSecret())
        service.setDefaultScope(facebookProperties.permissions)
        return service
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
    @Autowired
    Facebook facebook(ConnectionRepository connectionRepository) {
        Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class)
        return connection?.api
    }
}
