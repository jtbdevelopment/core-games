package com.jtbdevelopment.games.push.rest;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.players.notifications.RegisteredDevice;
import com.jtbdevelopment.games.push.PushProperties;
import com.jtbdevelopment.games.rest.services.SecurityService;
import com.jtbdevelopment.games.security.SessionUserInfo;
import com.jtbdevelopment.games.stringimpl.StringPlayer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * Date: 10/16/15 Time: 7:05 PM
 */
public class PushServicesTest {

  private String SENDERID = "SENDERID";
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private PushProperties properties = new PushProperties(SENDERID, "X");
  private SessionUserInfo session = Mockito.mock(SessionUserInfo.class);
  private PushServices services = new PushServices(properties, playerRepository);

  @Before
  public void setup() {
    SecurityContextHolder.setContext(new SecurityContextImpl());
    when(session.getEffectiveUser()).thenReturn(PONE);
    when(session.getSessionUser()).thenReturn(PONE);
    TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(session, null);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }

  @Test
  public void testClassAnnotations() {
    assertTrue(SecurityService.class.isAnnotationPresent(RolesAllowed.class));
    assertArrayEquals(Collections.singletonList(PlayerRoles.PLAYER).toArray(),
        SecurityService.class.getAnnotation(RolesAllowed.class).value());
    assertTrue(SecurityService.class.isAnnotationPresent(Path.class));
    assertEquals("security", SecurityService.class.getAnnotation(Path.class).value());
  }

  @Test
  public void testSenderIDAnnotations() throws NoSuchMethodException {
    Method m = PushServices.class.getMethod("senderID");
    assertNotNull(m);
    assertTrue(m.isAnnotationPresent(GET.class));
    assertArrayEquals(Collections.singletonList(MediaType.TEXT_PLAIN).toArray(),
        m.getAnnotation(Produces.class).value());
    assertEquals("senderID", m.getAnnotation(Path.class).value());

  }

  @Test
  public void testSenderID() {
    assertEquals(SENDERID, services.senderID());
  }

  @Test
  public void testAddingDeviceAnnotations() throws NoSuchMethodException {
    Method m = PushServices.class.getMethod("registerDevice", new Class[]{String.class});
    assertEquals(3, m.getAnnotations().length);
    assertTrue(m.isAnnotationPresent(PUT.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        m.getAnnotation(Produces.class).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("register/{deviceID}", m.getAnnotation(Path.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(1, params.length);
    assertEquals(1, params[0].length);
    assertEquals(PathParam.class, params[0][0].annotationType());
    assertEquals("deviceID", ((PathParam) params[0][0]).value());
  }

  @Test
  public void testAddingDevice() {
    final RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID("some id over here");
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    final Player saved = new StringPlayer();
    when(playerRepository.save(PONE)).then(invocation -> {
      Player p = (Player) invocation.getArguments()[0];
      assertTrue(p.getRegisteredDevices().contains(device));
      return saved;
    });
    Assert.assertSame(saved, services.registerDevice(device.getDeviceID()));
  }

  @Test
  public void testRemoveDeviceAnnotations() throws NoSuchMethodException {
    Method m = PushServices.class.getMethod("unregisteredDevice", new Class[]{String.class});
    assertEquals(3, m.getAnnotations().length);
    assertTrue(m.isAnnotationPresent(PUT.class));
    assertTrue(m.isAnnotationPresent(Produces.class));
    assertArrayEquals(
        Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        m.getAnnotation(Produces.class).value());
    assertTrue(m.isAnnotationPresent(Path.class));
    assertEquals("unregister/{deviceID}", m.getAnnotation(Path.class).value());
    Annotation[][] params = m.getParameterAnnotations();
    assertEquals(1, params.length);
    assertEquals(1, params[0].length);
    assertEquals(PathParam.class, params[0][0].annotationType());
    assertEquals("deviceID", ((PathParam) params[0][0]).value());
  }

  @Test
  public void testRemovedDevice() {
    RegisteredDevice device = new RegisteredDevice();
    device.setDeviceID("some id over here");
    PONE.updateRegisteredDevice(device);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    final Player saved = new StringPlayer();
    when(playerRepository.save(PONE)).then(invocation -> {
      Player p = (Player) invocation.getArguments()[0];
      assertTrue(p.getRegisteredDevices().isEmpty());
      return saved;
    });
    Assert.assertSame(saved, services.unregisteredDevice(device.getDeviceID()));
  }
}
