package com.jtbdevelopment.games.rest.services;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;

import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * Date: 12/24/14 Time: 2:57 PM
 */
public class SecurityServiceTest {

  private SecurityService service = new SecurityService();

  @Test
  public void testClassAnnotations() {
    Assert.assertTrue(SecurityService.class.isAnnotationPresent(RolesAllowed.class));
    Assert.assertArrayEquals(new ArrayList<String>(Arrays.asList(PlayerRoles.PLAYER)).toArray(),
        SecurityService.class.getAnnotation(RolesAllowed.class).value());
    Assert.assertTrue(SecurityService.class.isAnnotationPresent(Path.class));
    Assert.assertEquals("security", SecurityService.class.getAnnotation(Path.class).value());
  }

  @Test
  public void testGetSessionPlayer() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    SessionUserInfo session = Mockito.mock(SessionUserInfo.class);
    Mockito.when(session.getEffectiveUser()).thenReturn(PONE);
    Mockito.when(session.getSessionUser()).thenReturn(PONE);
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(session, null));
    Assert.assertEquals(PONE, service.getSessionPlayer());
  }

  @Test
  public void testSessionPlayerAnnotations() throws NoSuchMethodException {
    Method m = SecurityService.class.getMethod("getSessionPlayer", new Class<?>[0]);
    Assert.assertEquals(2, m.getAnnotations().length);

    Assert.assertTrue(m.isAnnotationPresent(GET.class));
    Assert.assertTrue(m.isAnnotationPresent(Produces.class));
    Assert.assertArrayEquals(
        new ArrayList<String>(Arrays.asList(MediaType.APPLICATION_JSON)).toArray(),
        m.getAnnotation(Produces.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    Assert.assertEquals(0, params.length);
  }
}
