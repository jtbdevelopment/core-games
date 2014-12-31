package com.jtbdevelopment.games.security.spring.social

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.security.SocialAuthenticationServiceRegistry
import org.springframework.social.security.provider.SocialAuthenticationService

/**
 * Date: 12/16/14
 * Time: 12:56 PM
 */
@Configuration
@CompileStatic
class SpringSocialConfig {
    @Bean
    @Autowired
    SocialAuthenticationServiceRegistry connectionFactoryLocator(final List<SocialAuthenticationService> services) {
        SocialAuthenticationServiceRegistry socialAuthenticationServiceRegistry = new SocialAuthenticationServiceRegistry();
        for (final SocialAuthenticationService service : services) {
            socialAuthenticationServiceRegistry.addAuthenticationService(service);
        }
        return socialAuthenticationServiceRegistry;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
    @Autowired
    public ConnectionRepository connectionRepository(final UsersConnectionRepository userConnectionRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication == null) {
            return null;
        }
        return userConnectionRepository.createConnectionRepository(authentication.name)
    }
}
