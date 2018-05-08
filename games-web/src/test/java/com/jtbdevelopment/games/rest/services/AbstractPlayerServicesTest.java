package com.jtbdevelopment.games.rest.services;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.players.friendfinder.FriendFinder;
import com.jtbdevelopment.games.security.SessionUserInfo;
import com.jtbdevelopment.games.stringimpl.StringToStringConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * Date: 11/15/2014 Time: 12:02 PM
 */
public class AbstractPlayerServicesTest {

  private AbstractGameServices gamePlayServices = Mockito.mock(AbstractGameServices.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractAdminServices adminServices = Mockito.mock(AbstractAdminServices.class);
  private ApplicationContext context = Mockito.mock(ApplicationContext.class);
  private AbstractPlayerServices playerServices = new AbstractPlayerServices(gamePlayServices,
      playerRepository, adminServices, new StringToStringConverter()) {
  };
  private ThreadLocal<String> playerID = new ThreadLocal<>();
  private ThreadLocal<String> gameID = new ThreadLocal<>();

  @Before
  public void setup() {
    playerServices.setApplicationContext(context);
    Mockito.when(gamePlayServices.getPlayerID()).thenReturn(playerID);
    Mockito.when(gamePlayServices.getGameID()).thenReturn(gameID);
  }

  @Test
  public void testValidPlayer() {
    String APLAYER = "PLAYER";
    String AGAME = "GAME";
    playerServices.getPlayerID().set(APLAYER);
    Assert.assertSame(gamePlayServices, playerServices.gamePlay(AGAME));
    Assert.assertEquals(APLAYER, playerID.get());
    Assert.assertEquals(GameCoreTestCase.reverse(AGAME), gameID.get());
  }

  @Test
  public void testNullGame() {
    String APLAYER = "APLAYER";
    playerServices.getPlayerID().set(APLAYER);

    OutboundJaxrsResponse resp = (OutboundJaxrsResponse) playerServices.gamePlay(null);
    Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    Assert.assertEquals("Missing game identity", resp.getEntity());
  }

  @Test
  public void testEmptyGame() {
    String APLAYER = "APLAYER";
    playerServices.getPlayerID().set(APLAYER);

    OutboundJaxrsResponse resp = (OutboundJaxrsResponse) playerServices.gamePlay("   ");
    Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    Assert.assertEquals("Missing game identity", resp.getEntity());
  }

  @Test
  public void testGamePlayAnnotations() throws NoSuchMethodException {
    Method gamePlay = AbstractPlayerServices.class.getMethod("gamePlay", new Class[]{String.class});
    Assert.assertEquals(1, gamePlay.getAnnotations().length);
    Assert.assertTrue(gamePlay.isAnnotationPresent(Path.class));
    Assert.assertEquals("game/{gameID}", gamePlay.getAnnotation(Path.class).value());
    Annotation[][] params = gamePlay.getParameterAnnotations();
    Assert.assertEquals(1, params.length);
    Assert.assertEquals(1, params[0].length);
    Assert.assertEquals(PathParam.class, params[0][0].annotationType());
    Assert.assertEquals("gameID", ((PathParam) params[0][0]).value());
  }

  @Test
  public void testPlayerInfo() {
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    playerServices.getPlayerID().set(PONE.getIdAsString());

    Assert.assertEquals(PONE, playerServices.playerInfo());
  }

  @Test
  public void testPlayerInfoAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractPlayerServices.class.getMethod("playerInfo", new Class[0]);
    Assert.assertEquals(2, gameServices.getAnnotations().length);
    Assert.assertTrue(gameServices.isAnnotationPresent(Produces.class));
    Assert.assertArrayEquals(
        Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        gameServices.getAnnotation(Produces.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(GET.class));
    Annotation[][] params = gameServices.getParameterAnnotations();
    Assert.assertEquals(0, params.length);
  }

  @Test
  public void testUpdateLastVersionInfo() {
    final String newNotes = "NewVersion";
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    Mockito.when(playerRepository.save(PONE)).then(invocation -> {
      Assert.assertEquals(newNotes, PONE.getLastVersionNotes());
      return PTWO;
    });
    playerServices.getPlayerID().set(PONE.getIdAsString());

    Assert.assertEquals(PTWO, playerServices.updateLastVersionNotes(newNotes));
  }

  @Test
  public void testUpdateVersionNotesInfoAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractPlayerServices.class
        .getMethod("updateLastVersionNotes", new Class[]{String.class});
    Assert.assertEquals(3, gameServices.getAnnotations().length);
    Assert.assertTrue(gameServices.isAnnotationPresent(Produces.class));
    Assert.assertArrayEquals(
        Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        gameServices.getAnnotation(Produces.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(POST.class));
    Annotation[][] params = gameServices.getParameterAnnotations();
    Assert.assertEquals(1, params.length);
    Assert.assertEquals(1, params[0].length);
    Assert.assertEquals(PathParam.class, params[0][0].annotationType());
    Assert.assertEquals("versionNotes", ((PathParam) params[0][0]).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(Path.class));
    Assert.assertEquals("lastVersionNotes/{versionNotes}",
        gameServices.getAnnotation(Path.class).value());
  }

  @Test
  public void testGetFriendsV2() {
    String id = PFOUR.getId();
    playerServices.getPlayerID().set(id);
    FriendFinder friendFinder = Mockito.mock(FriendFinder.class);
    Map<String, Set<? super Object>> map = new HashMap<>();
    map.put("1", new HashSet<>(Collections.singletonList("2")));
    map.put("3", new HashSet<>(Arrays.asList("4", "X")));
    map.put("5", new HashSet<>(Collections.singletonList("6")));
    Mockito.when(friendFinder.findFriendsV2(id)).thenReturn(map);
    Mockito.when(context.getBean(FriendFinder.class)).thenReturn(friendFinder);
    assert map.equals(playerServices.getFriendsV2());
  }

  @Test
  public void testFriendsV2InfoAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractPlayerServices.class.getMethod("getFriendsV2", new Class[0]);
    Assert.assertEquals(3, gameServices.getAnnotations().length);
    Assert.assertTrue(gameServices.isAnnotationPresent(Path.class));
    Assert.assertEquals("friendsV2", gameServices.getAnnotation(Path.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(Produces.class));
    Assert.assertArrayEquals(
        Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        gameServices.getAnnotation(Produces.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(GET.class));
    Annotation[][] params = gameServices.getParameterAnnotations();
    Assert.assertEquals(0, params.length);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetFriendsV2NoAppContext() {
    playerServices.setApplicationContext(null);
    playerServices.getFriendsV2();
  }

  @Test
  public void testAdminServicesAnnotation() throws NoSuchMethodException {
    Method gameServices = AbstractPlayerServices.class.getMethod("adminServices", new Class[0]);
    Assert.assertEquals(2, gameServices.getAnnotations().length);
    Assert.assertTrue(gameServices.isAnnotationPresent(Path.class));
    Assert.assertEquals("admin", gameServices.getAnnotation(Path.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(RolesAllowed.class));
    Assert.assertArrayEquals(Collections.singletonList(PlayerRoles.ADMIN).toArray(),
        gameServices.getAnnotation(RolesAllowed.class).value());
    Annotation[][] params = gameServices.getParameterAnnotations();
    Assert.assertEquals(0, params.length);
  }

  @Test
  public void testAdminServices() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    SessionUserInfo sessionUserInfo = Mockito.mock(SessionUserInfo.class);
    Mockito.when(sessionUserInfo.getSessionUser()).thenReturn(PTWO);
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(sessionUserInfo, null));
    Assert.assertSame(adminServices, playerServices.adminServices());
  }
}
