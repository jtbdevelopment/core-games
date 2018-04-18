package com.jtbdevelopment.games.rest.handlers;

import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.masking.MaskedGame;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

/**
 * Date: 11/19/14 Time: 7:08 AM
 */
@Component
public class PlayerGamesFinderHandler extends AbstractGameGetterHandler {

  public static final ZoneId GMT = ZoneId.of("GMT");
  public static final Sort SORT = new Sort(Direction.DESC,
      new ArrayList<>(Arrays.asList("lastUpdate", "created")));
  private static int DEFAULT_PAGE_SIZE = 20;
  private static int DEFAULT_PAGE = 0;
  public static final PageRequest PAGE = PageRequest.of(DEFAULT_PAGE, DEFAULT_PAGE_SIZE, SORT);
  @Autowired
  protected GameMasker gameMasker;

  public List<MaskedGame> findGames(final Serializable playerID) {
    final Player player = loadPlayer(playerID);
    final ZonedDateTime now = ZonedDateTime.now(GMT);

    final List<MaskedGame> result = new ArrayList<>();
    Arrays.stream(GamePhase.values()).forEach(phase -> {
      ZonedDateTime days = now.minusDays(phase.getHistoryCutoffDays());
      List<MultiPlayerGame> games = ((AbstractMultiPlayerGameRepository) getGameRepository())
          .findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(player.getId(), phase,
              days.toInstant(), PAGE);
      result.addAll(games
          .stream()
          .map(game -> gameMasker.maskGameForPlayer(game, player))
          .collect(Collectors.toList()));
    });
    return result;
  }
}
