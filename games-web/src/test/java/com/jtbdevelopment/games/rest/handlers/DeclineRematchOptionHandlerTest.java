package com.jtbdevelopment.games.rest.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 8/23/15 Time: 5:17 PM
 */
public class DeclineRematchOptionHandlerTest {

  private DeclineRematchOptionHandler handler = new DeclineRematchOptionHandler();

  @Test
  public void testThrowsExceptionIfGameNotInRoundOverPhase() {
    Arrays.stream(GamePhase.values())
        .filter(phase -> !GamePhase.RoundOver.equals(phase))
        .forEach(gamePhase -> {
          StringMPGame game = GameCoreTestCase.makeSimpleMPGame("X");
          game.setGamePhase(gamePhase);
          try {
            handler.handleActionInternal(null, game, null);
            Assert.fail("Should have exceptioned in phase " + gamePhase);
          } catch (GameIsNotAvailableToRematchException e) {
            //
          }

        });
  }

  @Test
  public void testMarksGameAsNextRoundStarted() {
    StringMPGame game = GameCoreTestCase.makeSimpleMPGame("X");
    game.setGamePhase(GamePhase.RoundOver);
    assertSame(game, handler.handleActionInternal(null, game, null));
    assertEquals(GamePhase.NextRoundStarted, game.getGamePhase());
  }
}
