package com.jtbdevelopment.games.security.spring.social

import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnectionFactory
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.connect.UsersConnectionRepository
import org.springframework.social.security.SocialAuthenticationServiceRegistry
import org.springframework.social.security.provider.SocialAuthenticationService

import java.lang.reflect.Method

/**
 * Date: 1/8/15
 * Time: 6:53 AM
 */
class SpringSocialConfigTest extends GroovyTestCase {
    SpringSocialConfig config = new SpringSocialConfig()

    void testClassAnnotations() {
        assert SpringSocialConfig.class.getAnnotation(Configuration.class)
    }

    void testConnectionFactoryLocator() {
        SocialAuthenticationService service1 = [
                getConnectionFactory: {
                    return new FakeFacebookConnectionFactory()
                }
        ] as SocialAuthenticationService
        SocialAuthenticationService service2 = [
                getConnectionFactory: {
                    return new FakeTwitterConnectionFactory()
                }
        ] as SocialAuthenticationService
        SocialAuthenticationServiceRegistry registry = config.socialAuthenticationServiceLocator([service1, service2])
        assert registry.getAuthenticationService(FakeFacebookApi.FACEBOOK).is(service1)
        assert registry.getAuthenticationService(FakeTwitterApi.TWITTER).is(service2)
    }

    void testConnectionFactoryLocatorAnnotations() {
        Method m = SpringSocialConfig.class.getMethod('socialAuthenticationServiceLocator', [List.class] as Class[])
        assert m.getAnnotation(Autowired.class)
        assert m.getAnnotation(Bean.class)
    }

    void testConnectionRepositoryWithNullAuthentication() {
        SecurityContextHolder.context = new SecurityContextImpl()
        assertNull config.connectionRepository(null)
    }

    void testConnectionRepositoryWithAuthentication() {
        SecurityContextHolder.context = new SecurityContextImpl()
        String name = "name"
        def authentication = [
                getName: {
                    return name
                }
        ] as Authentication
        SecurityContextHolder.context.setAuthentication(authentication)
        def factory = [] as ConnectionRepository
        def connectionRepository = [
                createConnectionRepository: {
                    String it ->
                        assert it == name
                        return factory
                }
        ] as UsersConnectionRepository
        assert factory.is(config.connectionRepository(connectionRepository))
    }

    void testConnectionRepositoryAnnotations() {
        Method m = SpringSocialConfig.class.getMethod('connectionRepository', [UsersConnectionRepository.class] as Class[])
        assert m.getAnnotation(Autowired.class)
        assert m.getAnnotation(Bean.class)
        Scope scope = m.getAnnotation(Scope.class)
        assert scope
        assert scope.proxyMode() == ScopedProxyMode.INTERFACES
        assert scope.value() == ConfigurableBeanFactory.SCOPE_PROTOTYPE
    }
}
