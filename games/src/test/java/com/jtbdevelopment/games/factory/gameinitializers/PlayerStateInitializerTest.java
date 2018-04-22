package com.jtbdevelopment.games.factory.gameinitializers;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 4/4/2015 Time: 8:21 PM
 */
public class PlayerStateInitializerTest {

  private PlayerStateInitializer playerStateInitializer = new PlayerStateInitializer();

  @Test
  public void testInitializesAllPlayersToPendingAcceptingInitiatingPlayer() {
    final StringMPGame game = new StringMPGame();
    List<Player<String>> players = Arrays.asList(PONE, PTWO, PTHREE, PFOUR);
    game.setPlayers(players);
    game.setInitiatingPlayer(PTHREE.getId());
    playerStateInitializer.initializeGame(game);
    TestCase.assertEquals(4, game.getPlayerStates().size());
    players.forEach(p -> {
      PlayerState expected = PTHREE.equals(p) ? PlayerState.Accepted : PlayerState.Pending;
      Assert.assertEquals(expected, game.getPlayerStates().get(p.getId()));
    });
  }

  @Test
  public void testIgnoresInitializesSingePlayerGame() {
    StringSPGame game = new StringSPGame();
    playerStateInitializer.initializeGame(game);
  }
}
