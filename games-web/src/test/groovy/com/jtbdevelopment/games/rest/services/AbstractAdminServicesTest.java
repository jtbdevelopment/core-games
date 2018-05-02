package com.jtbdevelopment.games.rest.services;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import com.jtbdevelopment.games.stringimpl.StringToStringConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * Date: 12/24/14 Time: 2:23 PM
 */
public class AbstractAdminServicesTest {

  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractGameRepository gameRepository = Mockito.mock(AbstractGameRepository.class);
  private AbstractAdminServices adminServices = new AbstractAdminServices(playerRepository,
      gameRepository, new StringToStringConverter()) {
  };

  @Test
  public void testClassAnnotations() {
    assertTrue(AbstractAdminServices.class.isAnnotationPresent(RolesAllowed.class));
    assertArrayEquals(Collections.singletonList(PlayerRoles.ADMIN).toArray(),
        AbstractAdminServices.class.getAnnotation(RolesAllowed.class).value());
  }

  @Test
  public void testPlayerCount() {
    long expectedCount = 5;
    Mockito.when(playerRepository.count()).thenReturn(expectedCount);
    assertEquals(expectedCount, adminServices.players());
  }

  @Test
  public void testGetPlayersAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class.getMethod("players", new Class<?>[0]);
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(GET.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        m.getAnnotation(Produces.class).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("playerCount", m.getAnnotation(Path.class).value());
  }

  @Test
  public void testGameCount() {
    long expectedCount = 15;
    Mockito.when(gameRepository.count()).thenReturn(expectedCount);
    assertEquals(expectedCount, adminServices.games());
  }

  @Test
  public void testGetGamesAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class.getMethod("games", new Class<?>[0]);
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(GET.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        m.getAnnotation(Produces.class).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("gameCount", m.getAnnotation(Path.class).value());
  }

  @Test
  public void testPlayerCreatedSinceCount() {
    long expectedCount = 5;
    Instant since = Instant.ofEpochSecond(Instant.now().getEpochSecond());
    Mockito.when(playerRepository.countByCreatedGreaterThan(since)).thenReturn(expectedCount);
    assertEquals(expectedCount, adminServices.playersCreatedSince(since.getEpochSecond()));
  }

  @Test
  public void testGetPlayersCreatedSinceAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class
        .getMethod("playersCreatedSince", new Class[]{long.class});
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(GET.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        m.getAnnotation(Produces.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(1, params.length);
    assertEquals(1, params[0].length);
    assertEquals(PathParam.class, params[0][0].annotationType());
    assertEquals("since", ((PathParam) params[0][0]).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("playersCreated/{since}", m.getAnnotation(Path.class).value());
  }

  @Test
  public void testPlayerLastLoginSinceCount() {
    long expectedCount = 5;
    Instant since = Instant.ofEpochSecond(Instant.now().getEpochSecond());
    Mockito.when(playerRepository.countByLastLoginGreaterThan(since)).thenReturn(expectedCount);
    assertEquals(expectedCount, adminServices.playersLoggedInSince(since.getEpochSecond()));
  }

  @Test
  public void testGetPlayersLastLoginSinceAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class
        .getMethod("playersLoggedInSince", new Class[]{long.class});
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(GET.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        m.getAnnotation(Produces.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(1, params.length);
    assertEquals(1, params[0].length);
    assertEquals(PathParam.class, params[0][0].annotationType());
    assertEquals("since", ((PathParam) params[0][0]).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("playersLoggedIn/{since}", m.getAnnotation(Path.class).value());
  }

  @Test
  public void testGamesSinceCount() {
    long expectedCount = 5;

    Instant since = Instant.ofEpochSecond(Instant.now().getEpochSecond());
    Mockito.when(gameRepository.countByCreatedGreaterThan(since)).thenReturn(expectedCount);
    assertEquals(expectedCount, adminServices.gamesSince(since.getEpochSecond()));
  }

  @Test
  public void testGamesSinceAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class.getMethod("gamesSince", new Class[]{long.class});
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(GET.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        m.getAnnotation(Produces.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(1, params.length);
    assertEquals(1, params[0].length);
    assertEquals(PathParam.class, params[0][0].annotationType());
    assertEquals("since", ((PathParam) params[0][0]).value());
    assertEquals("gamesSince/{since}", m.getAnnotation(Path.class).value());
  }

  @Test
  public void testPlayersToSimulateLikeNoPageParams() {
    PageImpl<Player> repoResult = new PageImpl<>(Arrays.asList(PTWO, PTHREE));
    String likeString = "Hey Joe";
    PageRequest expectedPage = PageRequest
        .of(0, 500, new Sort(Direction.ASC, "displayName"));
    Mockito.when(playerRepository.findByDisplayNameContains(likeString, expectedPage))
        .thenReturn(repoResult);

    assertEquals(repoResult, adminServices.playersToSimulateLike(likeString, null, null));
  }

  @Test
  public void testPlayersToSimulateLikeAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class.getMethod("playersToSimulateLike",
        new Class[]{String.class, Integer.class, Integer.class});
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(GET.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        m.getAnnotation(Produces.class).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("playersLike", m.getAnnotation(Path.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(3, params.length);
    assertEquals(1, params[0].length);
    assertEquals(1, params[1].length);
    assertEquals(1, params[2].length);
    assertEquals(QueryParam.class, params[0][0].annotationType());
    assertEquals(QueryParam.class, params[1][0].annotationType());
    assertEquals(QueryParam.class, params[2][0].annotationType());
    assertEquals("like", ((QueryParam) params[0][0]).value());
    assertEquals("page", ((QueryParam) params[1][0]).value());
    assertEquals("pageSize", ((QueryParam) params[2][0]).value());
  }

  @Test
  public void testSwitchEffectiveUser() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    SessionUserInfo session = Mockito.mock(SessionUserInfo.class);
    Mockito.when(session.getSessionUser()).thenReturn(PONE);
    Mockito.when(session.getEffectiveUser()).thenReturn(PONE);
    TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    Mockito.when(playerRepository.findById(GameCoreTestCase.reverse(PTWO.getId())))
        .thenReturn(Optional.of(PTWO));

    Assert.assertSame(PTWO, adminServices.switchEffectiveUser(PTWO.getIdAsString()));
    Mockito.verify(session).setEffectiveUser(PTWO);
  }

  @Test
  public void testSwitchEffectiveUserBadID() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    SessionUserInfo session = Mockito.mock(SessionUserInfo.class);
    Mockito.when(session.getSessionUser()).thenReturn(PONE);
    Mockito.when(session.getEffectiveUser()).thenReturn(PONE);
    TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    Mockito.when(playerRepository.findById(GameCoreTestCase.reverse(PTWO.getId())))
        .thenReturn(Optional.empty());

    Response response = (Response) adminServices.switchEffectiveUser(PTWO.getIdAsString());
    Mockito.verify(session, Mockito.never()).setEffectiveUser(PTWO);
    assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
  }

  @Test
  public void testSwitchAnnotations() throws NoSuchMethodException {
    Method m = AbstractAdminServices.class
        .getMethod("switchEffectiveUser", new Class[]{String.class});
    assertEquals(3, DefaultGroovyMethods.size(m.getAnnotations()));
    assertTrue(m.isAnnotationPresent(PUT.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        m.getAnnotation(Produces.class).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("{playerID}", m.getAnnotation(Path.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(1, params.length);
    assertEquals(1, params[0].length);
    assertEquals(PathParam.class, params[0][0].annotationType());
    assertEquals("playerID", ((PathParam) params[0][0]).value());
  }
}
