package com.jtbdevelopment.games.security.spring.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.core.spring.security.crypto.password.InjectedBCryptPasswordEncoder;
import com.jtbdevelopment.games.security.spring.userdetails.PlayerUserDetailsService;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/12/15 Time: 7:01 PM
 */
public class SecurityConfigTest {

  private SecurityConfig securityConfig = new SecurityConfig();

  @Test
  public void testClassAnnotations() {
    assertTrue(SecurityConfig.class.isAnnotationPresent(Configuration.class));
    assertTrue(SecurityConfig.class.isAnnotationPresent(EnableWebSecurity.class));
    assertTrue(SecurityConfig.class.isAnnotationPresent(EnableGlobalMethodSecurity.class));
    assertTrue(
        SecurityConfig.class.getAnnotation(EnableGlobalMethodSecurity.class).jsr250Enabled());
  }

  @Test
  public void testSetsUpAuthenticationDatabase() throws Exception {
    Method m = SecurityConfig.class.getMethod("configureAuthenticationProvider");
    assertNotNull(m);
    assertTrue(m.isAnnotationPresent(PostConstruct.class));

    InjectedBCryptPasswordEncoder passwordEncoder = Mockito
        .mock(InjectedBCryptPasswordEncoder.class);
    Mockito.when(passwordEncoder.encode("userNotFoundPassword")).thenReturn("XYZ");
    Mockito.when(passwordEncoder.encode("test")).thenReturn("pass");
    securityConfig.injectedPasswordEncoder = passwordEncoder;
    final PlayerUserDetailsService userDetailsService = Mockito
        .mock(PlayerUserDetailsService.class);
    securityConfig.playerUserDetailsService = userDetailsService;

    final AuthenticationManagerBuilder managerBuilder = Mockito
        .mock(AuthenticationManagerBuilder.class);
    Mockito
        .when(managerBuilder.authenticationProvider(Matchers.isA(DaoAuthenticationProvider.class)))
        .then(invocation -> {
          DaoAuthenticationProvider daoAuthenticationProvider = (DaoAuthenticationProvider) invocation
              .getArguments()[0];
          PasswordEncoder encoder = (PasswordEncoder) ReflectionTestUtils
              .getField(daoAuthenticationProvider, "passwordEncoder");
          Assert.assertEquals("pass", encoder.encodePassword("test", null));
          Assert.assertSame(userDetailsService,
              ReflectionTestUtils.getField(daoAuthenticationProvider, "userDetailsService"));
          return managerBuilder;
        });
    securityConfig.authenticationManagerBuilder = managerBuilder;

    securityConfig.configureAuthenticationProvider();

    Mockito.verify(managerBuilder)
        .authenticationProvider(Matchers.isA(DaoAuthenticationProvider.class));
  }

  @Test
  public void testCsrfRepositoryTokenHeader() {
    HttpSessionCsrfTokenRepository repository = (HttpSessionCsrfTokenRepository) securityConfig
        .csrfTokenRepository();
    Assert.assertEquals("X-XSRF-TOKEN", ReflectionTestUtils.getField(repository, "headerName"));
  }
}
