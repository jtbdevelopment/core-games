package com.jtbdevelopment.games.security.spring.social.facebook

import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookAuthenticationService
import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookConnectionFactory
import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookServiceProvider
import com.jtbdevelopment.games.security.spring.social.facebook.provider.FacebookTokenExchangingOAuth2Template
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.social.connect.Connection
import org.springframework.social.connect.ConnectionFactory
import org.springframework.social.connect.ConnectionRepository
import org.springframework.social.facebook.api.Facebook
import org.springframework.social.oauth2.OAuth2Template
import org.springframework.social.security.provider.OAuth2AuthenticationService

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Date: 1/7/15
 * Time: 7:02 PM
 */
class FacebookConfigTest extends GroovyTestCase {
    FacebookConfig config = new FacebookConfig()

    void testClassAnnotations() {
        assert FacebookConfig.class.getAnnotation(Configuration.class)
    }

    void testFacebookAuthenticationService() {
        FacebookProperties properties = new FacebookProperties()
        properties.clientID = 'APRODUCT'
        properties.clientSecret = 'ASECRET'
        properties.permissions = 'PERMS'
        CustomFacebookAuthenticationService service = config.facebookAuthenticationService(properties)
        assert service
        CustomFacebookConnectionFactory factory = service.getConnectionFactory()
        Method m = ConnectionFactory.class.getDeclaredMethod('getServiceProvider', [] as Class[])
        m.accessible = true
        CustomFacebookServiceProvider provider = m.invoke(factory)
        FacebookTokenExchangingOAuth2Template template = provider.getOAuthOperations()
        Field f = OAuth2Template.class.getDeclaredField('clientId')
        f.accessible = true
        assert properties.clientID == f.get(template)
        f = OAuth2Template.class.getDeclaredField('clientSecret')
        f.accessible = true
        assert properties.clientSecret == f.get(template)
        f = OAuth2AuthenticationService.class.getDeclaredField('defaultScope')
        f.accessible = true
        assert properties.permissions == f.get(service)
    }

    void testFacebookAuthenticationServiceAnnotations() {
        Method m = FacebookConfig.class.getMethod('facebook', [ConnectionRepository.class] as Class[])
        assert m.getAnnotation(Autowired.class)
        assert m.getAnnotation(Bean.class)
    }

    void testFacebookWhenFound() {
        def facebookApi = [] as Facebook
        ConnectionRepository repository = [
                findPrimaryConnection: {
                    Class c ->
                        assert Facebook.class.is(c)
                        return [
                                getApi: {
                                    return facebookApi
                                }
                        ] as Connection
                }
        ] as ConnectionRepository

        facebookApi.is(config.facebook(repository))
    }

    void testFacebookWhenNotFound() {
        ConnectionRepository repository = [
                findPrimaryConnection: {
                    Class c ->
                        assert Facebook.class.is(c)
                        return null;
                }
        ] as ConnectionRepository

        assertNull config.facebook(repository)
    }

    void testFacebookAnnotations() {
        Method m = FacebookConfig.class.getMethod('facebook', [ConnectionRepository.class] as Class[])
        assert m.getAnnotation(Autowired.class)
        assert m.getAnnotation(Bean.class)
        Scope scope = m.getAnnotation(Scope.class)
        assert scope
        assert scope.proxyMode() == ScopedProxyMode.INTERFACES
        assert scope.value() == ConfigurableBeanFactory.SCOPE_PROTOTYPE
    }
}
