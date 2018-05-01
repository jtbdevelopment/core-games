package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException;
import com.jtbdevelopment.games.exceptions.system.FailedToFindGameException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.SinglePlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.masking.MaskedGame;
import com.jtbdevelopment.games.state.transition.AbstractMPGamePhaseTransitionEngine;
import com.jtbdevelopment.games.state.transition.AbstractSPGamePhaseTransitionEngine;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 11/10/14 Time: 7:06 PM
 */
public class AbstractGameActionHandlerTest {

  private String testParam = "TESTPARAM";
  private StringMPGame handledMPGame = GameCoreTestCase.makeSimpleMPGame("gameMP");
  private StringMPGame mpGameParam = GameCoreTestCase.makeSimpleMPGame("paramMP");
  private StringSPGame handledSPGame = GameCoreTestCase.makeSimpleSPGame("gameSP");
  private StringSPGame spGameParam = GameCoreTestCase.makeSimpleSPGame("paramSP");
  private String gameId = "238njcn33";
  private GameEligibilityTracker eligibilityTracker = Mockito.mock(GameEligibilityTracker.class);
  private AbstractPlayerRepository playerRepostiory = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractSPGamePhaseTransitionEngine spTransitionEngine = Mockito
      .mock(AbstractSPGamePhaseTransitionEngine.class);
  private AbstractMPGamePhaseTransitionEngine mpTransitionEngine = Mockito
      .mock(AbstractMPGamePhaseTransitionEngine.class);
  private AbstractMultiPlayerGameRepository mpGameRepository = Mockito
      .mock(AbstractMultiPlayerGameRepository.class);
  private AbstractSinglePlayerGameRepository spGameRepository = Mockito
      .mock(AbstractSinglePlayerGameRepository.class);
  private GamePublisher gamePublisher = Mockito.mock(GamePublisher.class);
  private GameMasker gameMasker = Mockito.mock(GameMasker.class);
  private TestSPHandler handlerSP = new TestSPHandler();
  private TestMPHandler handlerMP = new TestMPHandler();

  @Before
  public void setup() {
    handlerSP.transitionEngine = spTransitionEngine;
    handlerSP.gameRepository = spGameRepository;
    handlerSP.playerRepository = playerRepostiory;
    handlerSP.gamePublisher = gamePublisher;
    handlerSP.gameTracker = eligibilityTracker;
    handlerSP.gameMasker = gameMasker;
    handlerMP.transitionEngine = mpTransitionEngine;
    handlerMP.gameRepository = mpGameRepository;
    handlerMP.playerRepository = playerRepostiory;
    handlerMP.gamePublisher = gamePublisher;
    handlerMP.gameTracker = eligibilityTracker;
    handlerMP.gameMasker = gameMasker;
    when(playerRepostiory.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    mpGameParam.setPlayers(Arrays.asList(PONE, PTWO));
    spGameParam.setPlayer(PONE);
  }

  @Test
  public void testDefaultRequiresEligibility() {
    Assert.assertFalse(new AbstractGameActionHandler<String, Game>() {
      protected Game handleActionInternal(final Player player, final Game game,
          final String param) {
        return null;
      }

    }.requiresEligibilityCheck(null));
  }

  @Test
  public void testAbstractHandlerBasicWithAllFeaturesSinglePlayer() {
    Game saved = GameCoreTestCase.makeSimpleSPGame("saved");
    Game transitioned = GameCoreTestCase.makeSimpleSPGame("trans");
    Game published = GameCoreTestCase.makeSimpleSPGame("pub");
    MaskedGame maskedGame = GameCoreTestCase.makeSimpleMaskedSPGame("masked");
    when(spGameRepository.findById(gameId)).thenReturn(Optional.of(spGameParam));
    when(spGameRepository.save(transitioned)).thenReturn(saved);
    when(spTransitionEngine.evaluateGame(handledSPGame))
        .thenReturn((SinglePlayerGame) transitioned);
    when(gamePublisher.publish(saved, PONE)).thenReturn(published);
    when(gameMasker.maskGameForPlayer(published, PONE)).thenReturn(maskedGame);
    assertSame(maskedGame, handlerSP.handleAction(PONE.getId(), gameId, testParam));
  }

  @Test
  public void testAbstractHandlerBasicWithAllFeaturesMultiPlayer() {
    Game saved = GameCoreTestCase.makeSimpleMPGame("saved");
    Game transitioned = GameCoreTestCase.makeSimpleMPGame("trans");
    Game published = GameCoreTestCase.makeSimpleMPGame("pub");
    MaskedGame masked = GameCoreTestCase.makeSimpleMaskedSPGame("masked");
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.of(mpGameParam));
    when(mpGameRepository.save(transitioned)).thenReturn(saved);
    when(mpTransitionEngine.evaluateGame(handledMPGame))
        .thenReturn((MultiPlayerGame) transitioned);
    when(gamePublisher.publish(saved, PONE)).thenReturn(published);
    when(gameMasker.maskGameForPlayer(published, PONE)).thenReturn(masked);

    assertSame(masked, handlerMP.handleAction(PONE.getId(), gameId, testParam));
  }

  @Test
  public void testAbstractHandlerWithEligibilityCheckAndEligible() {
    handlerMP.setCheckEligibility(true);
    Game saved = GameCoreTestCase.makeSimpleMPGame("saved");
    Game transitioned = GameCoreTestCase.makeSimpleMPGame("trans");
    Game published = GameCoreTestCase.makeSimpleMPGame("pub");
    MaskedGame masked = GameCoreTestCase.makeSimpleMaskedSPGame("masked");
    PlayerGameEligibilityResult eligibility = new PlayerGameEligibilityResult();
    eligibility.setPlayer(PONE);
    eligibility.setEligibility(PlayerGameEligibility.FreeGameUsed);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibility);
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.of(mpGameParam));
    when(mpGameRepository.save(transitioned)).thenReturn(saved);
    when(mpTransitionEngine.evaluateGame(handledMPGame))
        .thenReturn((MultiPlayerGame) transitioned);
    when(gamePublisher.publish(saved, PONE)).thenReturn(published);
    when(gameMasker.maskGameForPlayer(published, PONE)).thenReturn(masked);

    assertSame(masked, handlerMP.handleAction(PONE.getId(), gameId, testParam));
  }

  @Test(expected = OutOfGamesForTodayException.class)
  public void testAbstractHandlerWithEligibilityCheckAndNotEligible() {

    handlerMP.setCheckEligibility(true);
    PlayerGameEligibilityResult eligibility = new PlayerGameEligibilityResult();
    eligibility.setPlayer(PONE);
    eligibility.setEligibility(PlayerGameEligibility.NoGamesAvailable);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibility);
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.of(mpGameParam));

    handlerMP.handleAction(PONE.getId(), gameId, testParam);
  }

  @Test(expected = IllegalStateException.class)
  public void testAbstractHandlerWithEligibilityCheckAndHandleInternalExceptions() {
    handlerSP.setCheckEligibility(true);
    handlerSP.setInternalException(true);

    handlerMP.setCheckEligibility(true);
    PlayerGameEligibilityResult eligibility = new PlayerGameEligibilityResult();
    eligibility.setPlayer(PONE);
    eligibility.setEligibility(PlayerGameEligibility.FreeGameUsed);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibility);
    when(spGameRepository.findById(gameId)).thenReturn(Optional.of(spGameParam));

    try {
      handlerSP.handleAction(PONE.getId(), gameId, testParam);
    } finally {
      verify(eligibilityTracker).revertGameEligibility(eligibility);
    }

  }

  @Test(expected = IllegalArgumentException.class)
  public void testAbstractHandlerWithEligibilityCheckAndTransitionExceptions() {
    PlayerGameEligibilityResult eligibility = new PlayerGameEligibilityResult();
    eligibility.setPlayer(PONE);
    eligibility.setEligibility(PlayerGameEligibility.FreeGameUsed);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibility);
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.of(mpGameParam));
    when(mpTransitionEngine.evaluateGame(handledMPGame))
        .thenThrow(new IllegalArgumentException());

    handlerMP.setCheckEligibility(true);

    try {
      handlerMP.handleAction(PONE.getId(), gameId, testParam);
    } finally {
      verify(eligibilityTracker).revertGameEligibility(eligibility);
    }

  }

  @Test(expected = IllegalArgumentException.class)
  public void testAbstractHandlerWithEligibilityCheckAndRevertExceptionsAlso() {
    PlayerGameEligibilityResult eligibility = new PlayerGameEligibilityResult();
    eligibility.setPlayer(PONE);
    eligibility.setEligibility(PlayerGameEligibility.FreeGameUsed);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibility);
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.of(mpGameParam));
    when(mpTransitionEngine.evaluateGame(handledMPGame))
        .thenThrow(new IllegalArgumentException());
    handlerMP.setCheckEligibility(true);

    Mockito.doThrow(new IllegalStateException()).when(eligibilityTracker)
        .revertGameEligibility(eligibility);
    handlerMP.handleAction(PONE.getId(), gameId, testParam);
  }

  @Test(expected = FailedToFindGameException.class)
  public void testAbstractHandlerCantLoadGame() {
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.empty());
    handlerMP.handleAction(PONE.getId(), gameId, testParam);
  }

  @Test(expected = PlayerNotPartOfGameException.class)
  public void testAbstractHandlerInvalidPlayer() {
    when(mpGameRepository.findById(gameId)).thenReturn(Optional.of(mpGameParam));
    when(playerRepostiory.findById(PTHREE.getId())).thenReturn(Optional.of(PTHREE));
    handlerMP.handleAction(PTHREE.getId(), gameId, testParam);
  }

  private class TestSPHandler extends AbstractGameActionHandler<String, Game> {

    private boolean checkEligibility = false;
    private boolean internalException = false;

    @Override
    protected boolean requiresEligibilityCheck(final String param) {
      return checkEligibility;
    }

    @Override
    protected Game handleActionInternal(final Player player, final Game game, final String param) {
      Assert.assertEquals(param, testParam);
      assertSame(game, spGameParam);
      if (internalException) {
        throw new IllegalStateException();
      }

      return handledSPGame;
    }

    public boolean getCheckEligibility() {
      return checkEligibility;
    }

    public boolean isCheckEligibility() {
      return checkEligibility;
    }

    public void setCheckEligibility(boolean checkEligibility) {
      this.checkEligibility = checkEligibility;
    }

    public boolean getInternalException() {
      return internalException;
    }

    public boolean isInternalException() {
      return internalException;
    }

    public void setInternalException(boolean internalException) {
      this.internalException = internalException;
    }
  }

  private class TestMPHandler extends AbstractGameActionHandler<String, Game> {

    private boolean checkEligibility = false;
    private boolean internalException = false;

    @Override
    protected boolean requiresEligibilityCheck(final String param) {
      return checkEligibility;
    }

    @Override
    protected Game handleActionInternal(final Player player, final Game game, final String param) {
      Assert.assertEquals(param, testParam);
      assertSame(game, mpGameParam);
      if (internalException) {
        throw new IllegalStateException();
      }

      return handledMPGame;
    }

    public boolean getCheckEligibility() {
      return checkEligibility;
    }

    public boolean isCheckEligibility() {
      return checkEligibility;
    }

    public void setCheckEligibility(boolean checkEligibility) {
      this.checkEligibility = checkEligibility;
    }

    public boolean getInternalException() {
      return internalException;
    }

    public boolean isInternalException() {
      return internalException;
    }

    public void setInternalException(boolean internalException) {
      this.internalException = internalException;
    }
  }
}
