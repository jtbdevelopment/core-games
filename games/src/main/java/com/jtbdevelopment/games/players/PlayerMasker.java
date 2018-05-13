package com.jtbdevelopment.games.players;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Date: 11/26/14 Time: 8:51 PM
 */
@Component
public class PlayerMasker<ID extends Serializable, P extends AbstractPlayer<ID>> {

  private static final String MASKED_MD5 = "md5";
  private static final String DISPLAY_NAME = "displayName";

  public List<Map<String, String>> maskFriendsV2(final Set<P> friends) {
    List<Map<String, String>> maskedFriends = new LinkedList<>();
    friends.forEach(friend -> {
      Map<String, String> maskedFriend = new HashMap<>();
      maskedFriend.put(MASKED_MD5, friend.getMd5());
      maskedFriend.put(DISPLAY_NAME, friend.getDisplayName());
      maskedFriends.add(maskedFriend);
    });
    return maskedFriends;
  }
}
