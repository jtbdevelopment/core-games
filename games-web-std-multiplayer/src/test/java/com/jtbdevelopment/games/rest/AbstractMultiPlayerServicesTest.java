package com.jtbdevelopment.games.rest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler;
import com.jtbdevelopment.games.state.masking.MaskedGame;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 4/8/2015 Time: 10:28 PM
 */
public class AbstractMultiPlayerServicesTest {

  private static final String PID = "PID-122";
  private PlayerGamesFinderHandler finderHandler = Mockito.mock(PlayerGamesFinderHandler.class);
  private AbstractMultiPlayerServices services = new AbstractMultiPlayerServices(null, null, null,
      null, finderHandler) {
  };

  @Before
  public void setUp() throws Exception {
    services.getPlayerID().set(PID);
  }

  @Test
  public void testGetGames() {
    String APLAYER = PID;
    List<MaskedGame> results = Arrays
        .asList(GameCoreTestCase.makeSimpleMaskedMPGame("1"),
            GameCoreTestCase.makeSimpleMaskedMPGame("2"),
            GameCoreTestCase.makeSimpleMaskedMPGame("3"));
    when(finderHandler.findGames(APLAYER)).thenReturn(results);
    services.getPlayerID().set(APLAYER);
    assertEquals(results, services.gamesForPlayer());
  }

  @Test
  public void testGamesAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractMultiPlayerServices.class
        .getMethod("gamesForPlayer", new Class[0]);
    assertEquals(3, gameServices.getAnnotations().length);
    assertTrue(gameServices.isAnnotationPresent(Path.class));
    assertEquals("games", gameServices.getAnnotation(Path.class).value());
    assertTrue(gameServices.isAnnotationPresent(Produces.class));
    assertArrayEquals(
        new ArrayList<String>(Arrays.asList(MediaType.APPLICATION_JSON)).toArray(),
        gameServices.getAnnotation(Produces.class).value());
    assertTrue(gameServices.isAnnotationPresent(GET.class));
    Annotation[][] params = gameServices.getParameterAnnotations();
    assertEquals(0, params.length);
  }
}
