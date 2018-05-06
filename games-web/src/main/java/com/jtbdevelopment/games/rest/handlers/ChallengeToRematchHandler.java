package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.rest.exceptions.GameIsNotAvailableToRematchException;
import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Date: 11/4/2014 Time: 9:11 PM TODO - multiplayer?  move?
 */
@Component
public class ChallengeToRematchHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractMultiPlayerGame<ID, FEATURES>,
    P extends Player<ID>>
    extends AbstractGameActionHandler<Object, ID, FEATURES, IMPL, P> {

  private final AbstractMultiPlayerGameFactory gameFactory;

  public ChallengeToRematchHandler(
      AbstractPlayerRepository<ID, P> playerRepository,
      AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      AbstractMultiPlayerGameFactory gameFactory) {
    super(playerRepository, gameRepository);
    this.gameFactory = gameFactory;
  }

  @Override
  protected boolean requiresEligibilityCheck(final Object param) {
    return true;
  }

  @Override
  protected IMPL handleActionInternal(
      final P player,
      final IMPL previousGame,
      final Object param) {
    if (!previousGame.getGamePhase().equals(GamePhase.RoundOver)) {
      throw new GameIsNotAvailableToRematchException();
    }

    previousGame.setRematchTimestamp(Instant.now());
    IMPL evaluateGame = (IMPL) transitionEngine.evaluateGame(previousGame);
    IMPL saved = gameRepository.save(evaluateGame);
    IMPL transitioned = (IMPL) gamePublisher.publish(saved, null);
    //  We set to system player so it gets published to all players, including this one
    //  TODO - handle newGame setup failing..
    return setupGame(transitioned, player);
  }

  private IMPL setupGame(final IMPL previousGame,
      final Player initiatingPlayer) {
    return (IMPL) gameFactory.createGame(previousGame, initiatingPlayer);
  }
}
