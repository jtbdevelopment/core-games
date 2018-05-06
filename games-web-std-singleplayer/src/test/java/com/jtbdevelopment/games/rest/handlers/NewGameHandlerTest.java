package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleSPGame;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import com.jtbdevelopment.games.factory.AbstractSinglePlayerGameFactory;
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.stringimpl.StringSPGame;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Date: 11/7/14 Time: 9:26 PM
 */
public class NewGameHandlerTest {

  private AbstractSinglePlayerGameRepository gameRepository = Mockito
      .mock(AbstractSinglePlayerGameRepository.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private GameTransitionEngine transitionEngine = Mockito.mock(GameTransitionEngine.class);
  private GameEligibilityTracker eligibilityTracker = Mockito.mock(GameEligibilityTracker.class);
  private GamePublisher gamePublisher = Mockito.mock(GamePublisher.class);
  private GameMasker gameMasker = Mockito.mock(GameMasker.class);
  private AbstractSinglePlayerGameFactory gameFactory = Mockito
      .mock(AbstractSinglePlayerGameFactory.class);
  private NewGameHandler handler = new NewGameHandler(playerRepository, gameFactory, gameRepository,
      transitionEngine,
      gameMasker, gamePublisher, eligibilityTracker);

  @Test
  public void testCreateGame() {
    Set<Object> features = new HashSet<>(
        Arrays.asList("GameFeature.SystemPuzzles", "GameFeature.Thieving"));
    StringSPGame game = makeSimpleSPGame("1");
    game.getFeatures().addAll(features);
    StringSPGame savedGame = makeSimpleSPGame("2");
    savedGame.setFeatures(features);
    StringSPGame transitionedGame = makeSimpleSPGame("3");
    StringSPGame publishedGame = makeSimpleSPGame("4");
    Mockito.when(gameFactory.createGame(features, PONE)).thenReturn(game);
    Mockito.when(gameRepository.save(transitionedGame)).thenReturn(savedGame);
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    Mockito.when(transitionEngine.evaluateGame(game)).thenReturn(transitionedGame);
    Mockito.when(gamePublisher.publish(savedGame, PONE)).thenReturn(publishedGame);
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(PONE);
    Mockito.when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);
    AbstractMaskedSinglePlayerGame maskedGame = new AbstractMaskedSinglePlayerGame() {
    };
    Mockito.when(gameMasker.maskGameForPlayer(savedGame, PONE)).thenReturn(maskedGame);
    Assert.assertSame(maskedGame, handler.handleCreateNewGame(PONE.getId(), features));
  }

  @Test
  public void testCreateGameAndTransitionExceptionsRevertsEligibility() {
    Set<Object> features = new HashSet<>(
        Arrays.asList("GameFeature.SystemPuzzles", "GameFeature.Thieving"));
    StringSPGame game = makeSimpleSPGame("");
    game.getFeatures().addAll(features);
    Mockito.when(gameFactory.createGame(features, PONE)).thenReturn(game);
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    Mockito.when(transitionEngine.evaluateGame(game)).thenThrow(new IllegalArgumentException());
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(PONE);
    Mockito.when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);
    try {
      handler.handleCreateNewGame(PONE.getId(), features);
      Assert.fail("should have errored");
    } catch (IllegalArgumentException e) {
      Mockito.verify(eligibilityTracker).revertGameEligibility(eligibilityResult);
    }

  }

  @Test
  public void testCreateGameRevertedAndRevertFails() {
    Set<Object> features = new HashSet<>(
        Arrays.asList("GameFeature.SystemPuzzles", "GameFeature.Thieving"));
    StringSPGame game = makeSimpleSPGame("");
    game.getFeatures().addAll(features);
    Mockito.when(gameFactory.createGame(features, PONE)).thenReturn(game);
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    Mockito.when(transitionEngine.evaluateGame(game)).thenThrow(new IllegalArgumentException());
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(PONE);
    Mockito.when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);
    Mockito.doThrow(new IllegalStateException()).when(eligibilityTracker)
        .revertGameEligibility(eligibilityResult);
    try {
      handler.handleCreateNewGame(PONE.getId(), features);
      Assert.fail("should have errored");
    } catch (IllegalStateException e) {
      Assert.fail("should have been illegal argument exception");
    } catch (IllegalArgumentException e) {
      //
    }

  }

  @Test
  public void testCreateGameFailsIfNotEligible() {
    Set<Object> features = new HashSet<>(
        Arrays.asList("GameFeature.SystemPuzzles", "GameFeature.Thieving"));
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.NoGamesAvailable);
    eligibilityResult.setPlayer(PONE);
    Mockito.when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);
    try {
      handler.handleCreateNewGame(PONE.getId(), features);
      Assert.fail("should have errored");
    } catch (OutOfGamesForTodayException e) {
      //
    }

  }

  @Test
  public void testInvalidInitiator() {
    Set<Object> features = new HashSet<>(
        Arrays.asList("GameFeature.SystemPuzzles", "GameFeature.Thieving"));
    Mockito.when(playerRepository.findById(PONE.getId())).thenReturn(Optional.empty());
    try {
      handler.handleCreateNewGame(PONE.getId(), features);
      Assert.fail("should have errored");
    } catch (FailedToFindPlayersException e) {
      //
    }

  }
}
