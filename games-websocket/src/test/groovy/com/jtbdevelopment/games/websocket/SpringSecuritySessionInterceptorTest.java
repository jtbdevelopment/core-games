package com.jtbdevelopment.games.websocket;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.security.SessionUserInfo;
import javax.servlet.http.HttpSession;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereRequestImpl.Builder;
import org.atmosphere.cpr.AtmosphereResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Date: 12/22/14 Time: 6:33 PM
 */
public class SpringSecuritySessionInterceptorTest {

  private SpringSecuritySessionInterceptor interceptor = new SpringSecuritySessionInterceptor();

  @Test
  public void testInspectWithCorrectDetails() {
    String id = "43rn";
    AtmosphereRequest request = new Builder().pathInfo("/" + id).build();
    Player player = Mockito.mock(Player.class);
    Mockito.when(player.getIdAsString()).thenReturn(id);
    SessionUserInfo userInfo = Mockito.mock(SessionUserInfo.class);
    Mockito.when(userInfo.getEffectiveUser()).thenReturn(player);
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getPrincipal()).thenReturn(userInfo);
    SecurityContext sc = Mockito.mock(SecurityContext.class);
    Mockito.when(sc.getAuthentication()).thenReturn(authentication);
    HttpSession httpSession = Mockito.mock(HttpSession.class);
    Mockito
        .when(httpSession.getAttribute(SpringSecuritySessionInterceptor.SPRING__SECURITY__CONTEXT))
        .thenReturn(sc);
    AtmosphereResource atmosphereResource = Mockito.mock(AtmosphereResource.class);
    Mockito.when(atmosphereResource.session()).thenReturn(httpSession);
    Mockito.when(atmosphereResource.getRequest()).thenReturn(request);
    Assert.assertEquals(Action.CONTINUE, interceptor.inspect(atmosphereResource));
  }

  @Test
  public void testInspectWithIncorrectDetails() {
    String id = "43rn";
    AtmosphereRequest request = new Builder().pathInfo("/X" + id).build();
    Player player = Mockito.mock(Player.class);
    Mockito.when(player.getIdAsString()).thenReturn(id);
    SessionUserInfo userInfo = Mockito.mock(SessionUserInfo.class);
    Mockito.when(userInfo.getEffectiveUser()).thenReturn(player);
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getPrincipal()).thenReturn(userInfo);
    SecurityContext sc = Mockito.mock(SecurityContext.class);
    Mockito.when(sc.getAuthentication()).thenReturn(authentication);
    HttpSession httpSession = Mockito.mock(HttpSession.class);
    Mockito
        .when(httpSession.getAttribute(SpringSecuritySessionInterceptor.SPRING__SECURITY__CONTEXT))
        .thenReturn(sc);
    AtmosphereResource atmosphereResource = Mockito.mock(AtmosphereResource.class);
    Mockito.when(atmosphereResource.session()).thenReturn(httpSession);
    Mockito.when(atmosphereResource.getRequest()).thenReturn(request);
    Assert.assertEquals(Action.CANCELLED, interceptor.inspect(atmosphereResource));
  }

  @Test
  public void testInspectWithNullContext() {
    String id = "43rn";
    AtmosphereRequest request = new Builder().pathInfo("/" + id).build();
    HttpSession httpSession = Mockito.mock(HttpSession.class);
    Mockito
        .when(httpSession.getAttribute(SpringSecuritySessionInterceptor.SPRING__SECURITY__CONTEXT))
        .thenReturn(null);
    AtmosphereResource atmosphereResource = Mockito.mock(AtmosphereResource.class);
    Mockito.when(atmosphereResource.session()).thenReturn(httpSession);
    Mockito.when(atmosphereResource.getRequest()).thenReturn(request);
    Assert.assertEquals(Action.CANCELLED, interceptor.inspect(atmosphereResource));
  }

  @Test
  public void testInspectWithNullSession() {
    String id = "43rn";
    AtmosphereRequest request = new Builder().pathInfo("/" + id).build();
    SecurityContext sc = Mockito.mock(SecurityContext.class);
    Mockito.when(sc.getAuthentication()).thenReturn(null);
    HttpSession httpSession = Mockito.mock(HttpSession.class);
    Mockito
        .when(httpSession.getAttribute(SpringSecuritySessionInterceptor.SPRING__SECURITY__CONTEXT))
        .thenReturn(sc);
    AtmosphereResource atmosphereResource = Mockito.mock(AtmosphereResource.class);
    Mockito.when(atmosphereResource.session()).thenReturn(httpSession);
    Mockito.when(atmosphereResource.getRequest()).thenReturn(request);

    Assert.assertEquals(Action.CANCELLED, interceptor.inspect(atmosphereResource));
  }
}
