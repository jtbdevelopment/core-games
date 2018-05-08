package com.jtbdevelopment.games.rest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler;
import com.jtbdevelopment.games.rest.handlers.ChallengeToRematchHandler;
import com.jtbdevelopment.games.rest.handlers.QuitHandler;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 4/8/2015 Time: 10:28 PM
 */
public class AbstractMultiPlayerGameServicesTest {

  private static final String PID = "PID-122";
  private static final String GID = "GID-111354";
  private final AbstractMaskedMultiPlayerGame result = new AbstractMaskedMultiPlayerGame() {
  };
  private ChallengeResponseHandler challengeResponseHandler = mock(ChallengeResponseHandler.class);
  private ChallengeToRematchHandler rematchHandler = Mockito.mock(ChallengeToRematchHandler.class);
  private QuitHandler quitHandler = Mockito.mock(QuitHandler.class);
  private AbstractMultiPlayerGameServices services = new AbstractMultiPlayerGameServices(
      null, null,
      challengeResponseHandler,
      rematchHandler,
      quitHandler) {
  };

  @Before
  public void setUp() throws Exception {
    services.getPlayerID().set(PID);
    services.getGameID().set(GID);
  }

  @Test
  public void testActionAnnotations() {
    Map<String, String> methods = new HashMap<String, String>() {{
      put("rejectGame", "reject");
      put("acceptGame", "accept");
      put("createRematch", "rematch");
      put("quitGame", "quit");
    }};
    methods.forEach((method, path) -> {
      final Method m;
      try {
        m = AbstractMultiPlayerGameServices.class.getMethod(method);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
      int expectedA = 3;
      assertEquals(expectedA, m.getAnnotations().length);
      assertTrue(m.isAnnotationPresent(PUT.class));
      assertTrue(m.isAnnotationPresent(Produces.class));
      assertArrayEquals(
          Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
          m.getAnnotation(Produces.class).value());
      assertTrue(m.isAnnotationPresent(Path.class));
      assertEquals(path, m.getAnnotation(Path.class).value());
    });

  }

  @Test
  public void testRejectGame() {
    when(challengeResponseHandler.handleAction(PID, GID, PlayerState.Rejected)).thenReturn(result);
    assertSame(result, services.rejectGame());
  }

  @Test
  public void testAcceptGame() {
    when(challengeResponseHandler.handleAction(PID, GID, PlayerState.Accepted)).thenReturn(result);
    assertSame(result, services.acceptGame());
  }

  @Test
  public void testCreateRematch() {
    Mockito.when(rematchHandler.handleAction(PID, GID)).thenReturn(result);
    Assert.assertSame(result, services.createRematch());
  }

  @Test
  public void testQuitGame() {
    Mockito.when(quitHandler.handleAction(PID, GID)).thenReturn(result);
    Assert.assertSame(result, services.quitGame());
  }

}
