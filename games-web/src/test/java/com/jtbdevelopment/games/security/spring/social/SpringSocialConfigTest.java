package com.jtbdevelopment.games.security.spring.social;

import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeFacebookConnectionFactory;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterApi;
import com.jtbdevelopment.core.spring.social.dao.utility.FakeTwitterConnectionFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.provider.SocialAuthenticationService;

/**
 * Date: 1/8/15 Time: 6:53 AM
 */
public class SpringSocialConfigTest {

  private SpringSocialConfig config = new SpringSocialConfig();

  @Test
  public void testClassAnnotations() {
    Assert.assertTrue(SpringSocialConfig.class.isAnnotationPresent(Configuration.class));
  }

  @Test
  public void testConnectionFactoryLocator() {
    SocialAuthenticationService service1 = Mockito.mock(SocialAuthenticationService.class);
    SocialAuthenticationService service2 = Mockito.mock(SocialAuthenticationService.class);
    Mockito.when(service1.getConnectionFactory()).thenReturn(new FakeFacebookConnectionFactory());
    Mockito.when(service2.getConnectionFactory()).thenReturn(new FakeTwitterConnectionFactory());
    SocialAuthenticationServiceRegistry registry = config.socialAuthenticationServiceLocator(
        new ArrayList<SocialAuthenticationService>(Arrays.asList(service1, service2)));
    Assert.assertSame(service1, registry.getAuthenticationService(FakeFacebookApi.FACEBOOK));
    Assert.assertSame(service2, registry.getAuthenticationService(FakeTwitterApi.TWITTER));
  }

  @Test
  public void testConnectionFactoryLocatorAnnotations() throws NoSuchMethodException {
    Method m = SpringSocialConfig.class
        .getMethod("socialAuthenticationServiceLocator", new Class[]{List.class});
    Assert.assertTrue(m.isAnnotationPresent(Autowired.class));
    Assert.assertTrue(m.isAnnotationPresent(Bean.class));
  }

  @Test
  public void testConnectionRepositoryWithNullAuthentication() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    Assert.assertNull(config.connectionRepository(null));
  }

  @Test
  public void testConnectionRepositoryWithAuthentication() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    String name = "name";
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getName()).thenReturn(name);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    ConnectionRepository factory = Mockito.mock(ConnectionRepository.class);
    UsersConnectionRepository connectionRepository = Mockito.mock(UsersConnectionRepository.class);
    Mockito.when(connectionRepository.createConnectionRepository(name)).thenReturn(factory);
    Assert.assertSame(factory, config.connectionRepository(connectionRepository));
  }

  @Test
  public void testConnectionRepositoryAnnotations() throws NoSuchMethodException {
    Method m = SpringSocialConfig.class
        .getMethod("connectionRepository", new Class[]{UsersConnectionRepository.class});
    Assert.assertTrue(m.isAnnotationPresent(Autowired.class));
    Assert.assertTrue(m.isAnnotationPresent(Bean.class));
    Scope scope = m.getAnnotation(Scope.class);
    Assert.assertNotNull(scope);
    Assert.assertEquals(ScopedProxyMode.INTERFACES, scope.proxyMode());
    Assert.assertEquals(ConfigurableBeanFactory.SCOPE_PROTOTYPE, scope.value());
  }
}
