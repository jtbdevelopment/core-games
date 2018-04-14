package com.jtbdevelopment.games.players.friendfinder;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Date: 11/26/14 Time: 1:09 PM
 */
@Component
public class ManualFriendFinder implements SourceBasedFriendFinder {

  private final AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository;

  public ManualFriendFinder(
      final AbstractPlayerRepository<? extends Serializable, ? extends Player> playerRepository) {
    this.playerRepository = playerRepository;
  }

  @Override
  public boolean handlesSource(final String source) {
    return ManualPlayer.MANUAL_SOURCE.equals(source);
  }

  @Override
  public Map<String, Set<? extends Player>> findFriends(final Player player) {
    Set<Player> friends = new HashSet<>(
        playerRepository.findBySourceAndDisabled(ManualPlayer.MANUAL_SOURCE, false));
    friends.remove(player);
    Map<String, Set<? extends Player>> result = new HashMap<>();
    result.put(FRIENDS_KEY, friends);
    return result;
  }
}
