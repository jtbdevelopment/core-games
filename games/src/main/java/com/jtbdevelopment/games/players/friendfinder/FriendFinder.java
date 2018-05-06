package com.jtbdevelopment.games.players.friendfinder;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import com.jtbdevelopment.games.players.AbstractPlayer;
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
public class FriendFinder<ID extends Serializable, P extends AbstractPlayer<ID>> {

  private final List<SourceBasedFriendFinder> friendFinders;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final PlayerMasker<ID, P> friendMasker;

  public FriendFinder(
      @SuppressWarnings("SpringJavaAutowiringInspection") final AbstractPlayerRepository<ID, P> playerRepository,
      final List<SourceBasedFriendFinder> friendFinders,
      final PlayerMasker<ID, P> friendMasker) {
    this.friendFinders = friendFinders;
    this.playerRepository = playerRepository;
    this.friendMasker = friendMasker;
  }

  public Map<String, Set<? super Object>> findFriendsV2(final ID playerId) {
    Player<ID> player = getPlayer(playerId);

    Map<String, Set<? super Object>> combinedFriends = combine(getFriendsFromEachFinder(player));

    combinedFriends.putIfAbsent(SourceBasedFriendFinder.MASKED_FRIENDS_KEY, new HashSet<>());
    if (combinedFriends.containsKey(SourceBasedFriendFinder.FRIENDS_KEY)) {
      //noinspection unchecked
      Set<P> players = combinedFriends.remove(SourceBasedFriendFinder.FRIENDS_KEY)
          .stream()
          .filter(o -> o instanceof Player)
          .map(o -> (P) o)
          .collect(Collectors.toSet());
      List<Map<String, String>> masked = friendMasker.maskFriendsV2(players);
      combinedFriends.get(SourceBasedFriendFinder.MASKED_FRIENDS_KEY).addAll(masked);
    }

    return combinedFriends;
  }

  private Map<String, Set<? super Object>> combine(List<Map<String, Set<?>>> finderFriends) {
    Map<String, Set<? super Object>> combinedFriends = new HashMap<>();
    finderFriends.forEach(subSet ->
        subSet.forEach((key, value) -> {
          combinedFriends.putIfAbsent(key, new HashSet<>());
          combinedFriends.get(key).addAll(value);
        }));
    return combinedFriends;
  }

  private List<Map<String, Set<?>>> getFriendsFromEachFinder(Player<ID> player) {
    return friendFinders.stream()
        .filter(finder -> finder.handlesSource(player.getSource()))
        .map(finder -> finder.findFriends(player))
        .collect(Collectors.toList());
  }

  private Player<ID> getPlayer(ID playerId) {
    Optional<P> optionalPlayer = playerRepository.findById(playerId);
    if (!optionalPlayer.isPresent() || optionalPlayer.get().isDisabled()) {
      throw new FailedToFindPlayersException();
    }

    return optionalPlayer.get();
  }
}
