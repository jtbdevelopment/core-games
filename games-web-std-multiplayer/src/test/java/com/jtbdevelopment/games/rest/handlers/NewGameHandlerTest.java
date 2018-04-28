package com.jtbdevelopment.games.rest.handlers;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.OutOfGamesForTodayException;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.masking.AbstractGameMasker;
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.stringimpl.StringMPGame;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import com.jtbdevelopment.games.tracking.PlayerGameEligibility;
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 11/7/14 Time: 9:26 PM
 */
public class NewGameHandlerTest {

  private Set<Object> features = new HashSet<>(
      Arrays.asList("GameFeature.SystemPuzzles", "GameFeature.Thieving"));
  private List<Player> players = Arrays.asList(PTWO, PTHREE, PFOUR);
  private List<String> md5s = Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5());
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private AbstractMultiPlayerGameRepository gameRepository = Mockito
      .mock(AbstractMultiPlayerGameRepository.class);
  private GameTransitionEngine transitionEngine = Mockito.mock(GameTransitionEngine.class);
  private AbstractMultiPlayerGameFactory gameFactory = Mockito
      .mock(AbstractMultiPlayerGameFactory.class);
  private AbstractGameMasker gameMasker = Mockito.mock(AbstractGameMasker.class);
  private GamePublisher gamePublisher = Mockito.mock(GamePublisher.class);
  private GameEligibilityTracker eligibilityTracker = Mockito.mock(GameEligibilityTracker.class);
  private NewGameHandler handler = new NewGameHandler(gameFactory, gameRepository, transitionEngine,
      gameMasker, gamePublisher, eligibilityTracker);

  @Before
  public void setup() {
    handler.playerRepository = playerRepository;
  }

  @Test
  public void testCreateGame() {
    StringMPGame game = GameCoreTestCase.makeSimpleMPGame("created");
    game.getFeatures().addAll(features);
    StringMPGame savedGame = GameCoreTestCase.makeSimpleMPGame("save");
    savedGame.setFeatures(features);
    StringMPGame transitionedGame = GameCoreTestCase.makeSimpleMPGame("engine");
    StringMPGame publishedGame = GameCoreTestCase.makeSimpleMPGame("pub");
    when(gameFactory.createGame(features, players, PONE)).thenReturn(game);
    when(gameRepository.save(transitionedGame)).thenReturn(savedGame);
    when(playerRepository.findByMd5In(Matchers
        .eq(Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5()))))
        .thenReturn(players);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    when(transitionEngine.evaluateGame(game)).thenReturn(transitionedGame);
    when(gamePublisher.publish(savedGame, PONE)).thenReturn(publishedGame);
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(PONE);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);
    AbstractMaskedMultiPlayerGame maskedGame = GameCoreTestCase.makeSimpleMaskedMPGame("mask");
    when(gameMasker.maskGameForPlayer(savedGame, PONE)).thenReturn(maskedGame);

    Assert.assertSame(maskedGame, handler.handleCreateNewGame(PONE.getId(), md5s, features));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGameAndTransitionExceptions() {
    StringMPGame game = GameCoreTestCase.makeSimpleMPGame("3");
    game.getFeatures().addAll(features);
    when(gameFactory.createGame(features, players, PONE)).thenReturn(game);
    when(playerRepository.findByMd5In(Matchers
        .eq(Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5()))))
        .thenReturn(players);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    when(transitionEngine.evaluateGame(game)).thenThrow(new IllegalArgumentException());
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(PONE);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);

    try {
      handler.handleCreateNewGame(PONE.getId(), md5s, features);
    } finally {
      verify(eligibilityTracker).revertGameEligibility(eligibilityResult);
    }

  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGameAndRevertExceptionWrapped() {
    StringMPGame game = GameCoreTestCase.makeSimpleMPGame("3");
    game.getFeatures().addAll(features);
    when(gameFactory.createGame(features, players, PONE)).thenReturn(game);
    when(playerRepository.findByMd5In(Matchers
        .eq(Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5()))))
        .thenReturn(players);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    when(transitionEngine.evaluateGame(game)).thenThrow(new IllegalArgumentException());
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.FreeGameUsed);
    eligibilityResult.setPlayer(PONE);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);
    Mockito.doThrow(new IllegalStateException()).when(eligibilityTracker)
        .revertGameEligibility(eligibilityResult);
    try {
      handler.handleCreateNewGame(PONE.getId(), md5s, features);
    } finally {
      verify(eligibilityTracker).revertGameEligibility(eligibilityResult);
    }

  }

  @Test(expected = OutOfGamesForTodayException.class)
  public void testCreateGameFailsIfNotEligible() {
    when(playerRepository.findByMd5In(Matchers
        .eq(Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5()))))
        .thenReturn(players);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.of(PONE));
    PlayerGameEligibilityResult eligibilityResult = new PlayerGameEligibilityResult();
    eligibilityResult.setEligibility(PlayerGameEligibility.NoGamesAvailable);
    eligibilityResult.setPlayer(PONE);
    when(eligibilityTracker.getGameEligibility(PONE)).thenReturn(eligibilityResult);

    try {
      handler.handleCreateNewGame(PONE.getId(), md5s, features);
    } finally {
      verify(eligibilityTracker, Mockito.never()).revertGameEligibility(eligibilityResult);
    }
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testInvalidInitiator() {
    when(playerRepository.findByMd5In(Matchers
        .eq(Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5()))))
        .thenReturn(players);
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.empty());

    handler.handleCreateNewGame(PONE.getId(), md5s, features);
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testNotAllPlayersFound() {
    when(playerRepository.findByMd5In(Matchers
        .eq(Arrays.asList(PTWO.getMd5(), PTHREE.getMd5(), PFOUR.getMd5()))))
        .thenReturn(Arrays.asList(PFOUR, PTWO));
    when(playerRepository.findById(PONE.getId())).thenReturn(Optional.empty());

    handler.handleCreateNewGame(PONE.getId(), md5s, features);

  }
}
