package com.jtbdevelopment.games.security.spring.security

import com.jtbdevelopment.core.spring.security.crypto.password.InjectedBCryptPasswordEncoder
import com.jtbdevelopment.games.security.spring.userdetails.PlayerUserDetailsService
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.test.util.ReflectionTestUtils

import javax.annotation.PostConstruct
import java.lang.reflect.Method

import static org.junit.Assert.*
import static org.mockito.Matchers.isA

/**
 * Date: 1/12/15
 * Time: 7:01 PM
 */
class SecurityConfigTest {
    private SecurityConfig securityConfig = new SecurityConfig()

    @Test
    void testClassAnnotations() {
        assertTrue SecurityConfig.class.isAnnotationPresent(Configuration.class)
        assertTrue SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class)
        assertTrue SecurityConfig.class.isAnnotationPresent(EnableGlobalMethodSecurity.class)
        assertTrue SecurityConfig.class.getAnnotation(EnableGlobalMethodSecurity.class).jsr250Enabled()
    }

    @Test
    void testSetsUpAuthenticationDatabase() {
        Method m = SecurityConfig.class.getMethod('configureAuthenticationProvider')
        assertNotNull m
        assertTrue m.isAnnotationPresent(PostConstruct.class)

        InjectedBCryptPasswordEncoder passwordEncoder = Mockito.mock(InjectedBCryptPasswordEncoder.class)
        Mockito.when(passwordEncoder.encode("userNotFoundPassword")).thenReturn("XYZ")
        Mockito.when(passwordEncoder.encode("test")).thenReturn("pass")
        securityConfig.injectedPasswordEncoder = passwordEncoder
        PlayerUserDetailsService userDetailsService = Mockito.mock(PlayerUserDetailsService.class)
        securityConfig.playerUserDetailsService = userDetailsService

        AuthenticationManagerBuilder managerBuilder = Mockito.mock(AuthenticationManagerBuilder.class)
        Mockito.when(managerBuilder.authenticationProvider(isA(DaoAuthenticationProvider.class))).then(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                DaoAuthenticationProvider daoAuthenticationProvider = invocation.arguments[0]
                def encoder = (PasswordEncoder) ReflectionTestUtils.getField(daoAuthenticationProvider, "passwordEncoder");
                assertEquals("pass", encoder.encodePassword("test", null));
                assertSame(userDetailsService, ReflectionTestUtils.getField(daoAuthenticationProvider, "userDetailsService"));
                return managerBuilder;
            }
        })
        securityConfig.authenticationManagerBuilder = managerBuilder

        securityConfig.configureAuthenticationProvider()

        Mockito.verify(managerBuilder).authenticationProvider(isA(DaoAuthenticationProvider.class))
    }

    @Test
    void testCsrfRepositoryTokenHeader() {
        HttpSessionCsrfTokenRepository repository = securityConfig.csrfTokenRepository()
        assertEquals 'X-XSRF-TOKEN', repository.headerName
    }
}
