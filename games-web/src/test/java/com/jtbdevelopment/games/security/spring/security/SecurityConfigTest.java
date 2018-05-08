package com.jtbdevelopment.games.security.spring.security;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.core.spring.security.crypto.password.InjectedBCryptPasswordEncoder;
import com.jtbdevelopment.games.security.spring.userdetails.PlayerUserDetailsService;
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

  private SecurityConfig securityConfig;

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
    InjectedBCryptPasswordEncoder passwordEncoder = mock(InjectedBCryptPasswordEncoder.class);
    when(passwordEncoder.encode("userNotFoundPassword")).thenReturn("XYZ");
    when(passwordEncoder.encode("test")).thenReturn("pass");
    final PlayerUserDetailsService userDetailsService = mock(PlayerUserDetailsService.class);

    final AuthenticationManagerBuilder managerBuilder = mock(AuthenticationManagerBuilder.class);
    when(managerBuilder.authenticationProvider(Matchers.isA(DaoAuthenticationProvider.class)))
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

    securityConfig = new SecurityConfig(userDetailsService, passwordEncoder, null, null,
        managerBuilder, null, null);
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
