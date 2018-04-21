package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;
import java.beans.Transient;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 1/7/15 Time: 6:54 AM
 */
public class AbstractMultiPlayerGameTest {

  @Test
  public void testConstructor() {
    IntegerMultiPlayerGame game = new IntegerMultiPlayerGame();
    Assert.assertNull(game.getInitiatingPlayer());
    Assert.assertTrue(game.getPlayers().isEmpty());
    Assert.assertTrue(game.getPlayerStates().isEmpty());
    Assert.assertNull(game.getDeclinedTimestamp());
    Assert.assertNull(game.getRematchTimestamp());
  }

  @Test
  public void testAllPlayers() {
    Player p1 = Mockito.mock(Player.class);
    Player p2 = Mockito.mock(Player.class);
    IntegerMultiPlayerGame game = new IntegerMultiPlayerGame();
    game.setPlayers(Arrays.asList(p1, p2));
    Assert.assertEquals(Arrays.asList(p1, p2), game.getPlayers());
    Assert.assertEquals(Arrays.asList(p1, p2), game.getAllPlayers());
  }

  private static class IntegerMultiPlayerGame extends AbstractMultiPlayerGame<Integer, Object> {

    private Integer id;
    private Integer previousId;

    @Override
    @Transient
    public String getIdAsString() {
      return id.toString();
    }

    @Override
    @Transient
    public String getPreviousIdAsString() {
      return previousId.toString();
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public Integer getPreviousId() {
      return previousId;
    }

    public void setPreviousId(Integer previousId) {
      this.previousId = previousId;
    }
  }
}
