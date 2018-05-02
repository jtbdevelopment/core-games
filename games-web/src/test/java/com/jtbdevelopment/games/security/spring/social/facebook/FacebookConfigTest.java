package com.jtbdevelopment.games.security.spring.social.facebook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookAuthenticationService;
import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookConnectionFactory;
import com.jtbdevelopment.games.security.spring.social.facebook.provider.CustomFacebookServiceProvider;
import com.jtbdevelopment.games.security.spring.social.facebook.provider.FacebookTokenExchangingOAuth2Template;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 1/7/15 Time: 7:02 PM
 */
public class FacebookConfigTest {

  private FacebookConfig config = new FacebookConfig();

  @Test
  public void testClassAnnotations() {
    Assert.assertTrue(FacebookConfig.class.isAnnotationPresent(Configuration.class));
  }

  @Test
  public void testFacebookAuthenticationService() {
    FacebookProperties properties = new FacebookProperties("APRODUCT", "ASECRET", "PERMS");
    CustomFacebookAuthenticationService service = config.facebookAuthenticationService(properties);
    assertNotNull(service);
    CustomFacebookConnectionFactory factory = (CustomFacebookConnectionFactory) service
        .getConnectionFactory();
    CustomFacebookServiceProvider provider = ReflectionTestUtils
        .invokeMethod(factory, "getServiceProvider");
    FacebookTokenExchangingOAuth2Template template = (FacebookTokenExchangingOAuth2Template) provider
        .getOAuthOperations();

    assertEquals(properties.getClientID(), ReflectionTestUtils.getField(template, "clientId"));
    assertEquals(properties.getClientSecret(),
        ReflectionTestUtils.getField(template, "clientSecret"));
    assertEquals(properties.getPermissions(),
        ReflectionTestUtils.getField(service, "defaultScope"));
  }

  @Test
  public void testFacebookAuthenticationServiceAnnotations() throws NoSuchMethodException {
    Method m = FacebookConfig.class.getMethod("facebook", new Class[]{ConnectionRepository.class});
    Assert.assertTrue(m.isAnnotationPresent(Autowired.class));
    Assert.assertTrue(m.isAnnotationPresent(Bean.class));
  }

  @Test
  public void testFacebookWhenFound() {
    Facebook facebook = Mockito.mock(Facebook.class);
    ConnectionRepository connectionRepository = Mockito.mock(ConnectionRepository.class);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(connectionRepository.findPrimaryConnection(Facebook.class)).thenReturn(connection);
    Mockito.when(connection.getApi()).thenReturn(facebook);

    Assert.assertSame(facebook, config.facebook(connectionRepository));
  }

  @Test
  public void testFacebookWhenNotFound() {
    ConnectionRepository connectionRepository = Mockito.mock(ConnectionRepository.class);
    Mockito.when(connectionRepository.findPrimaryConnection(Facebook.class)).thenReturn(null);
    Assert.assertNull(config.facebook(connectionRepository));
  }

  @Test
  public void testFacebookAnnotations() throws NoSuchMethodException {
    Method m = FacebookConfig.class.getMethod("facebook", new Class[]{ConnectionRepository.class});
    Assert.assertTrue(m.isAnnotationPresent(Autowired.class));
    Assert.assertTrue(m.isAnnotationPresent(Bean.class));
    Scope scope = m.getAnnotation(Scope.class);
    assertNotNull(scope);
    assertEquals(ScopedProxyMode.INTERFACES, scope.proxyMode());
    assertEquals(ConfigurableBeanFactory.SCOPE_PROTOTYPE, scope.value());
  }
}
