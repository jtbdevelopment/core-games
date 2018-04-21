package com.jtbdevelopment.games.players.friendfinder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.GameCoreTestCase;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.Player;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 11/26/14 Time: 1:12 PM
 */
public class ManualFriendFinderTest {

  private AbstractPlayerRepository abstractPlayerRepository = mock(AbstractPlayerRepository.class);
  private ManualFriendFinder finder = new ManualFriendFinder(abstractPlayerRepository);

  @Test
  public void testHandlesSource() {
    assert finder.handlesSource(ManualPlayer.MANUAL_SOURCE);
    Assert.assertFalse(finder.handlesSource("Facebook"));
  }

  @Test
  public void testFindFriends() {
    Player<String> playerA = GameCoreTestCase.makeSimplePlayer("a");
    Player<String> pX = GameCoreTestCase.makeSimplePlayer("b");
    Player<String> pY = GameCoreTestCase.makeSimplePlayer("c");
    Player<String> pZ = GameCoreTestCase.makeSimplePlayer("d");
    List<Player<String>> ps = Arrays.asList(pX, pY, pZ, playerA);
    when(abstractPlayerRepository.findBySourceAndDisabled(ManualPlayer.MANUAL_SOURCE, false))
        .thenReturn(ps);
    Map<String, Set<Player<String>>> map = new HashMap<>();
    map.put(SourceBasedFriendFinder.FRIENDS_KEY, new HashSet<>(Arrays.asList(pX, pY, pZ)));
    Assert.assertEquals(map, finder.findFriends(playerA));
  }
}
