package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertEquals;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotPossibleToQuitNowException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import java.util.Arrays;
import java.util.LinkedHashMap;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Date: 4/8/2015 Time: 10:02 PM
 */
public class QuitHandlerTest {

  private QuitHandler handler = new QuitHandler<>(null, null, null, null, null, null);

  @Test
  public void testExceptionsOnQuitRematchRematchedPhases() {
    Arrays.asList(GamePhase.Quit, GamePhase.RoundOver, GamePhase.NextRoundStarted,
        GamePhase.Declined).forEach(phase -> {

      StringMPGame game = GameCoreTestCase.makeSimpleMPGame(phase.toString());
      game.setGamePhase(phase);
      try {
        handler.handleActionInternal(null, game, null);
        TestCase.fail();
      } catch (GameIsNotPossibleToQuitNowException e) {
        //
      }

    });
  }

  @Test
  public void testQuitsGamesInOtherStates() {
    Arrays.asList(GamePhase.Challenged, GamePhase.Setup, GamePhase.Playing).forEach(it -> {
      StringMPGame game = GameCoreTestCase.makeSimpleMPGame(it.toString());
      LinkedHashMap<String, PlayerState> map = new LinkedHashMap<>(2);
      map.put(PONE.getId(), PlayerState.Pending);
      map.put(PTWO.getId(), PlayerState.Rejected);
      game.setPlayerStates(map);
      game.setGamePhase(it);

      StringMPGame ret = (StringMPGame) handler.handleActionInternal(PTWO, game, null);

      TestCase.assertSame(game, ret);
      assertEquals(GamePhase.Quit, game.getGamePhase());
      LinkedHashMap<String, PlayerState> map1 = new LinkedHashMap<>(2);
      map1.put(PONE.getId(), PlayerState.Pending);
      map1.put(PTWO.getId(), PlayerState.Quit);
      assertEquals(map1, game.getPlayerStates());
    });
  }
}
