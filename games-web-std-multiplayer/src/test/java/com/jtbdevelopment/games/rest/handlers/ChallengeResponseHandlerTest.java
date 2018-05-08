package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.exceptions.input.TooLateToRespondToChallengeException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 11/9/2014 Time: 8:21 PM
 */
public class ChallengeResponseHandlerTest {

  private ChallengeResponseHandler handler = new ChallengeResponseHandler(null, null, null, null,
      null, null);

  @Test
  public void testRequiresEligibilityForAcceptButNotOtherStates() {
    Arrays.stream(PlayerState.values()).forEach(state -> {
      Assert.assertEquals(PlayerState.Accepted.equals(state),
          handler.requiresEligibilityCheck((PlayerState) state));

    });
  }

  @Test
  public void testExceptionOnBadPhases() {
    Arrays.stream(GamePhase.values())
        .filter(state -> !(GamePhase.Declined.equals(state) || GamePhase.Challenged.equals(state)))
        .forEach(state -> {
          StringMPGame game = new StringMPGame();
          game.setGamePhase(state);
          try {
            handler.handleActionInternal(PONE, game, PlayerState.Rejected);
            Assert.fail("Should have exceptioned on state " + state);
          } catch (TooLateToRespondToChallengeException e) {
            //
          }

        });
  }

  @Test
  public void testSetsStateForPlayer() {
    Map<String, PlayerState> initialStates = new LinkedHashMap<>(4);
    initialStates.put(PONE.getId(), PlayerState.Pending);
    initialStates.put(PTWO.getId(), PlayerState.Rejected);
    initialStates.put(PTHREE.getId(), PlayerState.Pending);
    initialStates.put(PFOUR.getId(), PlayerState.Accepted);
    Arrays.asList(GamePhase.Challenged, GamePhase.Declined).forEach(gamePhase -> {
      Arrays.asList(PlayerState.Accepted, PlayerState.Rejected).forEach(newState -> {
        StringMPGame game = GameCoreTestCase.makeSimpleMPGame("341");
        game.setGamePhase(gamePhase);
        game.setPlayerStates(new HashMap<>(initialStates));
        handler.handleActionInternal(PONE, game, newState);
        Assert.assertEquals(newState, game.getPlayerStates().get(PONE.getId()));
        Assert.assertEquals(PlayerState.Rejected,
            game.getPlayerStates().get(PTWO.getId()));
        Assert.assertEquals(PlayerState.Pending,
            game.getPlayerStates().get(PTHREE.getId()));
        Assert.assertEquals(PlayerState.Accepted,
            game.getPlayerStates().get(PFOUR.getId()));

      });
    });
  }

  @Test
  public void testOverridesResponseForPlayer() {
    Map<String, PlayerState> initialStates = new LinkedHashMap<>(4);
    initialStates.put(PONE.getId(), PlayerState.Accepted);
    initialStates.put(PTWO.getId(), PlayerState.Rejected);
    initialStates.put(PTHREE.getId(), PlayerState.Pending);
    initialStates.put(PFOUR.getId(), PlayerState.Accepted);
    Arrays.asList(GamePhase.Challenged, GamePhase.Declined).forEach(gamePhase -> {
      Arrays.asList(PlayerState.Accepted, PlayerState.Rejected).forEach(newState -> {
        StringMPGame game = GameCoreTestCase.makeSimpleMPGame("352");
        game.setGamePhase(gamePhase);
        game.setPlayerStates(new HashMap<>(initialStates));
        handler.handleActionInternal(PONE, game, newState);
        Assert.assertEquals(newState, game.getPlayerStates().get(PONE.getId()));
        Assert.assertEquals(PlayerState.Rejected,
            game.getPlayerStates().get(PTWO.getId()));
        Assert.assertEquals(PlayerState.Pending,
            game.getPlayerStates().get(PTHREE.getId()));
        Assert.assertEquals(PlayerState.Accepted,
            game.getPlayerStates().get(PFOUR.getId()));

      });
    });
  }
}
