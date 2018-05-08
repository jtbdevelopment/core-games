package com.jtbdevelopment.games.rest.services;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import com.jtbdevelopment.games.state.GamePhase;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
 * Date: 11/15/2014 Time: 11:28 AM
 */
public class AbstractPlayerGatewayServiceTest {

  private AbstractPlayerServices playerServices = Mockito.mock(AbstractPlayerServices.class);
  private AbstractPlayerGatewayService playerGatewayService = new AbstractPlayerGatewayService(
      playerServices) {
  };

  @Test
  public void testClassAnnotations() {
    assertArrayEquals(Collections.singletonList(PlayerRoles.PLAYER).toArray(),
        AbstractPlayerGatewayService.class.getAnnotation(RolesAllowed.class).value());
  }

  @Test
  public void testPing() {
    assertEquals(playerGatewayService.ping(), AbstractPlayerGatewayService.PING_RESULT);
  }

  @Test
  public void testPingAnnotations() throws NoSuchMethodException {
    Method ping = AbstractPlayerGatewayService.class.getMethod("ping", new Class[0]);
    assertEquals(3, ping.getAnnotations().length);
    assertTrue(ping.isAnnotationPresent(GET.class));
    assertTrue(ping.isAnnotationPresent(Produces.class));
    assertArrayEquals(
        Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        ping.getAnnotation(Produces.class).value());
    assertTrue(ping.isAnnotationPresent(Path.class));
    assertEquals("ping", ping.getAnnotation(Path.class).value());
  }

  @Test
  public void testValidPlayer() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    SessionUserInfo userInfo = Mockito.mock(SessionUserInfo.class);
    Mockito.when(userInfo.getEffectiveUser()).thenReturn(PONE);
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(userInfo, null));
    ThreadLocal<Serializable> playerId = new ThreadLocal<Serializable>();
    Mockito.when(playerServices.getPlayerID()).thenReturn(playerId);

    Assert.assertSame(playerServices, playerGatewayService.gameServices());
    assertEquals(PONE.getId(), playerId.get());
  }

  @Test
  public void testGameServicesAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractPlayerGatewayService.class
        .getMethod("gameServices", new Class[0]);
    assertEquals(1, gameServices.getAnnotations().length);
    assertTrue(gameServices.isAnnotationPresent(Path.class));
    assertEquals("player", gameServices.getAnnotation(Path.class).value());
    Annotation[][] params = gameServices.getParameterAnnotations();
    assertEquals(0, params.length);
  }

  @Test
  public void testGetPhases() {
    LinkedHashMap<GamePhase, List<String>> map = new LinkedHashMap<GamePhase, List<String>>(7);
    map.put(GamePhase.Declined,
        Arrays.asList(GamePhase.Declined.getDescription(), GamePhase.Declined.getGroupLabel()));
    map.put(GamePhase.Playing,
        Arrays.asList(GamePhase.Playing.getDescription(), GamePhase.Playing.getGroupLabel()));
    map.put(GamePhase.Quit,
        Arrays.asList(GamePhase.Quit.getDescription(), GamePhase.Quit.getGroupLabel()));
    map.put(GamePhase.Setup,
        Arrays.asList(GamePhase.Setup.getDescription(), GamePhase.Setup.getGroupLabel()));
    map.put(GamePhase.NextRoundStarted, Arrays
        .asList(GamePhase.NextRoundStarted.getDescription(),
            GamePhase.NextRoundStarted.getGroupLabel()));
    map.put(GamePhase.RoundOver,
        Arrays.asList(GamePhase.RoundOver.getDescription(), GamePhase.RoundOver.getGroupLabel()));
    map.put(GamePhase.Challenged, Arrays
        .asList(GamePhase.Challenged.getDescription(), GamePhase.Challenged.getGroupLabel()));
    assertEquals(map, playerGatewayService.phasesAndDescriptions());
  }

  @Test
  public void testGetPhasesAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractPlayerGatewayService.class
        .getMethod("phasesAndDescriptions", new Class[0]);
    assertEquals(3, gameServices.getAnnotations().length);
    assertTrue(gameServices.isAnnotationPresent(Path.class));
    assertEquals("phases", gameServices.getAnnotation(Path.class).value());
    assertTrue(gameServices.isAnnotationPresent(GET.class));
    assertTrue(gameServices.isAnnotationPresent(Produces.class));
    assertArrayEquals(
        Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        gameServices.getAnnotation(Produces.class).value());
    Annotation[][] params = gameServices.getParameterAnnotations();
    assertEquals(0, params.length);
  }
}
