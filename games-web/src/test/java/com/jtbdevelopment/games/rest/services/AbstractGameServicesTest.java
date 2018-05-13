package com.jtbdevelopment.games.rest.services;

import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleMPGame;
import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleMaskedMPGame;

import com.jtbdevelopment.games.rest.handlers.DeclineRematchOptionHandler;
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringMaskedMPGame;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 3/27/15 Time: 6:54 PM
 */
public class AbstractGameServicesTest {

  private String PID = "4r3e";
  private String GID = "123d";
  private StringMPGame game = makeSimpleMPGame("MP");
  private StringMaskedMPGame maskedGame = makeSimpleMaskedMPGame("MMP");
  private GameGetterHandler gameGetterHandler = Mockito.mock(GameGetterHandler.class);
  private DeclineRematchOptionHandler declineRematchOptionHandler = Mockito
      .mock(DeclineRematchOptionHandler.class);
  private AbstractGameServices services = new AbstractGameServices(gameGetterHandler,
      declineRematchOptionHandler) {
  };

  @Before
  public void setUp() throws Exception {
    services.getPlayerID().set(PID);
    services.getGameID().set(GID);
  }

  @Test
  public void testGetGames() {
    Mockito.when(gameGetterHandler.getGame(PID, GID)).thenReturn(game);
    Assert.assertSame(game, services.getGame());
  }

  @Test
  public void testGetAnnotations() throws NoSuchMethodException {
    Method m = AbstractGameServices.class.getMethod("getGame", new Class[0]);
    Assert.assertEquals(2, m.getAnnotations().length);
    Assert.assertTrue(m.isAnnotationPresent(GET.class));
    Assert.assertTrue(m.isAnnotationPresent(Produces.class));
    Assert.assertArrayEquals(
        Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
        m.getAnnotation(Produces.class).value());
    Assert.assertFalse(m.isAnnotationPresent(Path.class));
  }

  @Test
  public void testActionAnnotations() {
    LinkedHashMap<String, String> map = new LinkedHashMap<>(3);
    map.put("endRematch", "endRematch");
    map.forEach((method, path) -> {
      Method m = null;
      try {
        m = AbstractGameServices.class.getMethod(method);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
      Assert.assertEquals(3, m.getAnnotations().length);
      Assert.assertTrue(m.isAnnotationPresent(PUT.class));
      Assert.assertTrue(m.isAnnotationPresent(Produces.class));
      Assert.assertArrayEquals(
          Collections.singletonList(MediaType.APPLICATION_JSON).toArray(),
          m.getAnnotation(Produces.class).value());
      Assert.assertTrue(m.isAnnotationPresent(Path.class));
      Assert.assertEquals(path, m.getAnnotation(Path.class).value());
    });
  }

  @Test
  public void testEndRematches() {
    Mockito.when(declineRematchOptionHandler.handleAction(PID, GID)).thenReturn(maskedGame);
    Assert.assertSame(maskedGame, services.endRematch());
  }
}
