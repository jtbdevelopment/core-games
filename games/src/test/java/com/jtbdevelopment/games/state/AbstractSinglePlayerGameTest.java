package com.jtbdevelopment.games.state;

import com.jtbdevelopment.games.players.Player;
import java.beans.Transient;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 1/7/15 Time: 6:57 AM
 */
public class AbstractSinglePlayerGameTest {

  @Test
  public void testConstructor() {
    FloatSinglePlayerGame game = new FloatSinglePlayerGame();

    Assert.assertNull(game.getPlayer());
  }

  @Test
  public void testGetPlayers() {
    Player player = Mockito.mock(Player.class);
    FloatSinglePlayerGame game = new FloatSinglePlayerGame();
    game.setPlayer(player);
    Assert.assertEquals(Arrays.asList(player), game.getAllPlayers());
  }

  private static class FloatSinglePlayerGame extends AbstractSinglePlayerGame<Float, Object> {

    private Float id;
    private Float previousId;

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

    public Float getId() {
      return id;
    }

    public void setId(Float id) {
      this.id = id;
    }

    public Float getPreviousId() {
      return previousId;
    }

    public void setPreviousId(Float previousId) {
      this.previousId = previousId;
    }
  }
}
