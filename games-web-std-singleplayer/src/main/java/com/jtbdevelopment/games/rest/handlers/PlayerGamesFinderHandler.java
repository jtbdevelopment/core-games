package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.AbstractSinglePlayerGameRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.masking.MaskedGame;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

/**
 * Date: 11/19/14 Time: 7:08 AM
 */
@Component
public class PlayerGamesFinderHandler<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractSinglePlayerGame<ID, FEATURES>,
    M extends AbstractMaskedSinglePlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameGetterHandler<ID, FEATURES, IMPL, P> {

  private static final ZoneId GMT = ZoneId.of("GMT");
  private static final Sort SORT = new Sort(Direction.DESC,
      new ArrayList<>(Arrays.asList("lastUpdate", "created")));
  private static int DEFAULT_PAGE_SIZE = 20;
  private static int DEFAULT_PAGE = 0;
  private static final PageRequest PAGE = PageRequest.of(DEFAULT_PAGE, DEFAULT_PAGE_SIZE, SORT);
  private final GameMasker<ID, IMPL, M> gameMasker;
  private final AbstractSinglePlayerGameRepository<ID, FEATURES, IMPL> singlePlayerGameRepository;

  PlayerGamesFinderHandler(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractSinglePlayerGameRepository<ID, FEATURES, IMPL> gameRepository,
      final GameMasker<ID, IMPL, M> gameMasker) {
    super(playerRepository, gameRepository);
    this.gameMasker = gameMasker;
    this.singlePlayerGameRepository = gameRepository;
  }

  public List<MaskedGame> findGames(final ID playerID) {
    final P player = loadPlayer(playerID);
    final ZonedDateTime now = ZonedDateTime.now(GMT);

    final List<MaskedGame> result = new ArrayList<>();
    Arrays.stream(GamePhase.values()).forEach(phase -> {
      ZonedDateTime days = now.minusDays(phase.getHistoryCutoffDays());
      List<IMPL> games = singlePlayerGameRepository
          .findByPlayerIdAndGamePhaseAndLastUpdateGreaterThan(player.getId(), phase,
              days.toInstant(), PAGE);
      result.addAll(games.stream().map(g -> gameMasker.maskGameForPlayer(g, player)).collect(
          Collectors.toList()));
    });
    return result;
  }
}
