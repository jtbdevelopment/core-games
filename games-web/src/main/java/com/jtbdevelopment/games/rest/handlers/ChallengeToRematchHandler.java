package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/2014 Time: 9:11 PM
 */
@Component
public class ChallengeToRematchHandler extends AbstractGameActionHandler<Object, MultiPlayerGame> {

  private final AbstractMultiPlayerGameFactory gameFactory;

  public ChallengeToRematchHandler(
      final AbstractMultiPlayerGameFactory gameFactory) {
    this.gameFactory = gameFactory;
  }

  @Override
  protected boolean requiresEligibilityCheck(final Object param) {
    return true;
  }

  @Override
  protected MultiPlayerGame handleActionInternal(final Player player,
      final MultiPlayerGame previousGame, final Object param) {
    if (!previousGame.getGamePhase().equals(GamePhase.RoundOver)) {
      throw new GameIsNotAvailableToRematchException();
    }

    previousGame.setRematchTimestamp(Instant.now());
    MultiPlayerGame transitioned = (MultiPlayerGame) gamePublisher
        .publish(gameRepository.save(transitionEngine.evaluateGame(previousGame)),
            null);
    //  We set to system player so it gets published to all players, including this one
    //  TODO - handle newGame setup failing..
    return setupGame(transitioned, player);
  }

  private MultiPlayerGame setupGame(final MultiPlayerGame previousGame,
      final Player initiatingPlayer) {
    return gameFactory.createGame(previousGame, initiatingPlayer);
  }
}
