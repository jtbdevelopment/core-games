package com.jtbdevelopment.games.players.friendfinder;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerMasker;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Date: 11/26/14 Time: 1:04 PM
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class FriendFinder {

  private final List<SourceBasedFriendFinder> friendFinders;
  private final AbstractPlayerRepository playerRepository;
  private final PlayerMasker friendMasker;

  public FriendFinder(
      final AbstractPlayerRepository playerRepository,
      final List<SourceBasedFriendFinder> friendFinders,
      final PlayerMasker friendMasker) {
    this.friendFinders = friendFinders;
    this.playerRepository = playerRepository;
    this.friendMasker = friendMasker;
  }

  public Map<String, Set<? super Object>> findFriendsV2(final Serializable playerId) {
    Optional<? extends Player> optionalPlayer = playerRepository.findById(playerId);
    if (!optionalPlayer.isPresent() || optionalPlayer.get().getDisabled()) {
      throw new FailedToFindPlayersException();
    }

    Player player = optionalPlayer.get();

    List<Map<String, Set<?>>> finderFriends = friendFinders.stream()
        .filter(finder -> finder.handlesSource(player.getSource()))
        .map(finder -> finder.findFriends(player))
        .collect(Collectors.toList());
    Map<String, Set<? super Object>> combinedFriends = new HashMap<>();
    finderFriends.forEach(subSet ->
        subSet.forEach((key, value) -> {
          combinedFriends.putIfAbsent(key, new HashSet<>());
          combinedFriends.get(key).addAll(value);
        }));

    combinedFriends.putIfAbsent(SourceBasedFriendFinder.MASKED_FRIENDS_KEY, new HashSet<>());
    Set removed = combinedFriends.remove(SourceBasedFriendFinder.FRIENDS_KEY);
    //noinspection unchecked
    Set<Player> players = (Set<Player>) removed;
    if (players != null) {
      List<Map<String, String>> masked = friendMasker.maskFriendsV2(players);
      combinedFriends.get(SourceBasedFriendFinder.MASKED_FRIENDS_KEY).addAll(masked);
    }

    return combinedFriends;
  }
}
