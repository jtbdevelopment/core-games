package com.jtbdevelopment.games.players.friendfinder;

import com.jtbdevelopment.games.players.Player;
import java.util.Map;
import java.util.Set;

/**
 * Date: 12/30/2014 Time: 12:03 PM
 */
public interface SourceBasedFriendFinder {

  String FRIENDS_KEY = "friends";
  String MASKED_FRIENDS_KEY = "maskedFriends";
  String INVITABLE_FRIENDS_KEY = "invitableFriends";
  String NOT_FOUND_KEY = "notFoundFriends";

  boolean handlesSource(final String source);

  /**
   * Return a set of data regarding friends At a minimum, the FRIENDS_KEY needs to be provided with
   * a list of Players
   */
  Map<String, Set<?>> findFriends(final Player player);
}
