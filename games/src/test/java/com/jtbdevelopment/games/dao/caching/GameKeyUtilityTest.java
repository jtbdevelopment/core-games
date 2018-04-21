package com.jtbdevelopment.games.dao.caching;

import com.jtbdevelopment.games.state.Game;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 3/2/15 Time: 8:49 PM
 */
public class GameKeyUtilityTest {

  @Test
  public void testCollectGameIDs() {
    Game g1 = Mockito.mock(Game.class);
    Game g2 = Mockito.mock(Game.class);
    Game g3 = Mockito.mock(Game.class);
    Mockito.when(g1.getId()).thenReturn("G1");
    Mockito.when(g2.getId()).thenReturn("G2");
    Mockito.when(g3.getId()).thenReturn("XX");
    Assert.assertEquals(Arrays.asList("G1", "G2", "XX"),
        GameKeyUtility.collectGameIDs(Arrays.asList(g1, g2, g3)));
  }

  @Test
  public void testCollectGameIDsNull() {
    Assert.assertTrue(GameKeyUtility.collectGameIDs(null).isEmpty());
  }

}
