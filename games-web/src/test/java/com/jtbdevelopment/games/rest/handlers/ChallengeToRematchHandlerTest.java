package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static org.mockito.Mockito.mock;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.transition.AbstractMPGamePhaseTransitionEngine;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import java.time.Instant;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Date: 4/8/2015 Time: 10:16 PM
 */
public class ChallengeToRematchHandlerTest {

  private AbstractMPGamePhaseTransitionEngine transitionEngine = mock(
      AbstractMPGamePhaseTransitionEngine.class);
  private AbstractMultiPlayerGameRepository gameRepository = mock(
      AbstractMultiPlayerGameRepository.class);
  private AbstractMultiPlayerGameFactory gameFactory = mock(AbstractMultiPlayerGameFactory.class);
  private GamePublisher gamePublisher = mock(GamePublisher.class);
  private ChallengeToRematchHandler handler = new ChallengeToRematchHandler(gameFactory);

  @Before
  public void setup() {
    handler.transitionEngine = transitionEngine;
    handler.gameRepository = gameRepository;
    handler.gamePublisher = gamePublisher;
  }

  @Test
  public void testEligibilityCheck() {
    Assert.assertTrue(handler.requiresEligibilityCheck(null));
    Assert.assertTrue(handler.requiresEligibilityCheck(""));
    Assert.assertTrue(handler.requiresEligibilityCheck(1L));
  }

  @Test
  public void testSetsUpRematch() throws InterruptedException {
    final Instant now = Instant.now();
    Thread.sleep(100);
    StringMPGame previous = GameCoreTestCase.makeSimpleMPGame("X");
    previous.setGamePhase(GamePhase.RoundOver);
    final StringMPGame previousT = GameCoreTestCase.makeSimpleMPGame("X");
    StringMPGame previousS = GameCoreTestCase.makeSimpleMPGame("X");
    StringMPGame previousP = GameCoreTestCase.makeSimpleMPGame("X");
    StringMPGame newGame = GameCoreTestCase.makeSimpleMPGame("NEW");
    newGame.setPreviousId(previous.getId());
    Mockito.when(gameFactory.createGame(previousP, PONE)).thenReturn(newGame);
    Mockito.when(transitionEngine.evaluateGame(previous)).then(new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        MultiPlayerGame game = (MultiPlayerGame) invocation.getArguments()[0];
        assert game.getRematchTimestamp() != null;
        assert now.compareTo((Instant) game.getRematchTimestamp()) < 0;
        return previousT;
      }

    });
    Mockito.when(gameRepository.save(previousT)).thenReturn(previousS);
    Mockito.when(gamePublisher.publish(previousS, null)).thenReturn(previousP);

    Assert.assertSame(newGame, handler.handleActionInternal(PONE, previous, null));
  }

  @Test
  public void testNotInRematchPhase() {
    Arrays.stream(GamePhase.values())
        .filter(phase -> !GamePhase.RoundOver.equals(phase))
        .forEach(phase -> {
          StringMPGame previous = GameCoreTestCase.makeSimpleMPGame("X");
          previous.setGamePhase(phase);
          try {
            handler.handleActionInternal(PONE, previous, null);
            Assert.fail("Should have exceptioned in phase " + phase);
          } catch (GameIsNotAvailableToRematchException e) {
            //
          }
        });
  }
}
