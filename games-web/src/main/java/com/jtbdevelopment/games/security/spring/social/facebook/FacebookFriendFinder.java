package com.jtbdevelopment.games.security.spring.social.facebook;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Reference;
import org.springframework.stereotype.Component;

/**
 * Date: 12/20/2014 Time: 11:17 PM
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class FacebookFriendFinder<ID extends Serializable, P extends AbstractPlayer<ID>>
    implements SourceBasedFriendFinder {

  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final Facebook facebook;

  public FacebookFriendFinder(
      @SuppressWarnings("SpringJavaAutowiringInspection") final AbstractPlayerRepository<ID, P> playerRepository,
      @Autowired(required = false) final Facebook facebook) {
    this.playerRepository = playerRepository;
    this.facebook = facebook;
  }

  @Override
  public boolean handlesSource(final String source) {
    return "facebook".equals(source) && facebook != null;
  }

  @Override
  public Map<String, Set<?>> findFriends(final Player player) {
    Map<String, Set<Object>> results = new HashMap<>();
    results.put(FRIENDS_KEY, new LinkedHashSet<>());
    results.put(NOT_FOUND_KEY, new LinkedHashSet<>());
    results.put(INVITABLE_FRIENDS_KEY, new LinkedHashSet<>());

    PagedList<Reference> friends = facebook.friendOperations().getFriends();
    List<String> friendSourceIds = friends.stream()
        .map(Reference::getId)
        .collect(Collectors.toList());

    List<P> players = playerRepository.findBySourceAndSourceIdIn("facebook", friendSourceIds);
    Map<String, P> sourceIdMap = players.stream()
        .collect(Collectors.toMap(AbstractPlayer::getSourceId, p -> p));
    friends.forEach(friendReference -> {
      Player friend = sourceIdMap.get(friendReference.getId());
      if (friend != null) {
        results.get(FRIENDS_KEY).add(friend);
      } else {
        results.get(NOT_FOUND_KEY).add(friendReference);
      }
    });
    PagedList<Reference> canInvite = facebook
        .fetchConnections("me", "invitable_friends", Reference.class);
    results.get(INVITABLE_FRIENDS_KEY).addAll(canInvite);
    return new HashMap<>(results);
  }
}
