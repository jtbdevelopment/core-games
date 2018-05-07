package com.jtbdevelopment.games.rest;

import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler;
import com.jtbdevelopment.games.state.masking.MaskedGame;
import com.jtbdevelopment.games.stringimpl.StringMaskedSPGame;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.GET;
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
public class AbstractSinglePlayerServicesTest {

  private static final String PID = "PID-122";
  private PlayerGamesFinderHandler finderHandler = Mockito.mock(PlayerGamesFinderHandler.class);
  private AbstractSinglePlayerServices services = new AbstractSinglePlayerServices(finderHandler) {
  };

  @Before
  public void setUp() throws Exception {
    services.getPlayerID().set(PID);
  }

  @Test
  public void testGetGames() {
    String APLAYER = PID;
    List<MaskedGame> results = Arrays.asList(
        new StringMaskedSPGame(),
        new StringMaskedSPGame(),
        new StringMaskedSPGame());
    when(finderHandler.findGames(APLAYER)).thenReturn(results);
    services.getPlayerID().set(APLAYER);
    Assert.assertSame(results, services.gamesForPlayer());
  }

  @Test
  public void testGamesAnnotations() throws NoSuchMethodException {
    Method gameServices = AbstractSinglePlayerServices.class
        .getMethod("gamesForPlayer", new Class[0]);
    Assert.assertEquals(3, gameServices.getAnnotations().length);
    Assert.assertTrue(gameServices.isAnnotationPresent(Path.class));
    Assert.assertEquals("games", gameServices.getAnnotation(Path.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(Produces.class));
    Assert.assertArrayEquals(
        Arrays.asList(MediaType.APPLICATION_JSON).toArray(),
        gameServices.getAnnotation(Produces.class).value());
    Assert.assertTrue(gameServices.isAnnotationPresent(GET.class));
    Annotation[][] params = gameServices.getParameterAnnotations();
    Assert.assertEquals(0, params.length);
  }

}
