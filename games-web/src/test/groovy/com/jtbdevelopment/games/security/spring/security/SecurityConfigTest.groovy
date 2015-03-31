package com.jtbdevelopment.games.security.spring.security

import com.jtbdevelopment.core.spring.security.crypto.password.InjectedBCryptPasswordEncoder
import com.jtbdevelopment.games.security.spring.userdetails.PlayerUserDetailsService
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository

import javax.annotation.PostConstruct
import java.lang.reflect.Method

/**
 * Date: 1/12/15
 * Time: 7:01 PM
 */
class SecurityConfigTest extends GroovyTestCase {
    SecurityConfig securityConfig = new SecurityConfig()

    void testClassAnnotations() {
        assert SecurityConfig.class.isAnnotationPresent(Configuration.class)
        assert SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)
        assert SecurityConfig.class.isAnnotationPresent(EnableGlobalMethodSecurity.class)
        assert SecurityConfig.class.getAnnotation(EnableGlobalMethodSecurity.class).jsr250Enabled()
    }

    void testSetsUpAuthenticationDatabase() {
        Method m = SecurityConfig.class.getMethod('configureAuthenticationProvider')
        assert m
        assert m.isAnnotationPresent(PostConstruct.class)

        AuthenticationProvider provider
        boolean encodeCalled = false
        securityConfig.injectedPasswordEncoder = [
                encode: {
                    String pass ->
                        encodeCalled = true
                        return pass;
                }
        ] as InjectedBCryptPasswordEncoder
        securityConfig.playerUserDetailsService = [] as PlayerUserDetailsService
        securityConfig.authenticationManagerBuilder = new AuthenticationManagerBuilder([] as ObjectPostProcessor) {
            @Override
            AuthenticationManagerBuilder authenticationProvider(final AuthenticationProvider authenticationProvider) {
                provider = authenticationProvider
                return super.authenticationProvider(authenticationProvider)
            }

        }
        securityConfig.configureAuthenticationProvider()
        assert provider
        assert provider instanceof DaoAuthenticationProvider
        //  Not possible to directly validate
        //assert provider.passwordEncoder.is(securityConfig.injectedPasswordEncoder)
        assert encodeCalled
        assert provider.userDetailsService.is(securityConfig.playerUserDetailsService)
    }

    void testCsrfRepositoryTokenHeader() {
        HttpSessionCsrfTokenRepository repository = securityConfig.csrfTokenRepository()
        assert repository.headerName == 'X-XSRF-TOKEN'
    }
}
