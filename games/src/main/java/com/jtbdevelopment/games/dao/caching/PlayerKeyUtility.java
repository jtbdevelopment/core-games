package com.jtbdevelopment.games.dao.caching;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.Player;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Component;

/**
 * Date: 2/27/15 Time: 9:46 PM
 */
@Component
public class PlayerKeyUtility<ID extends Serializable, P extends Player<ID>> {

  private static AbstractPlayerRepository playerRepository;

  public PlayerKeyUtility(
      @SuppressWarnings("SpringJavaAutowiringInspection") final AbstractPlayerRepository<ID, P> playerRepository) {
    PlayerKeyUtility.playerRepository = playerRepository;
  }

  public static List<Serializable> collectPlayerIDs(final Iterable<Player<Serializable>> players) {
    if (players == null) {
      return Collections.emptyList();
    }

    return StreamSupport.stream(players.spliterator(), false)
        .map(Player::getId)
        .collect(Collectors.toList());
  }

  public static List<String> collectPlayerMD5s(final Iterable<Player<Serializable>> players) {
    if (players == null) {
      return Collections.emptyList();
    }

    return StreamSupport.stream(players.spliterator(), false)
        .map(Player::getMd5)
        .collect(Collectors.toList());
  }

  public static List<String> collectPlayerSourceAndSourceIDs(
      final Iterable<Player<Serializable>> players) {
    if (players == null) {
      return Collections.emptyList();
    }

    return StreamSupport.stream(players.spliterator(), false)
        .map(Player::getSourceAndSourceId)
        .collect(Collectors.toList());
  }

  public static List<String> collectSourceAndSourceIDs(
      final String source,
      final Iterable<String> sourceIds) {
    if (sourceIds == null || source == null) {
      return Collections.emptyList();
    }

    return StreamSupport.stream(sourceIds.spliterator(), false)
        .map(sourceId -> AbstractPlayer.getSourceAndSourceId(source, sourceId))
        .collect(Collectors.toList());
  }

  public static String md5FromID(final Serializable id) {
    Optional<? extends Player> optional = playerRepository.findById(id);
    if (optional.isPresent()) {
      return optional.get().getMd5();
    }

    return null;
  }

  public static String sourceAndSourceIDFromID(final Serializable id) {
    Optional<? extends Player> optional = playerRepository.findById(id);
    if (optional.isPresent()) {
      return optional.get().getSourceAndSourceId();
    }

    return null;
  }
}
