package com.jtbdevelopment.games.players.friendfinder;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PINACTIVE1;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException;
import com.jtbdevelopment.games.players.PlayerMasker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Date: 11/26/14 Time: 4:18 PM
 */
public class FriendFinderTest {

  private FriendFinder finder;

  @Test
  public void testClassAnnotations() {
    Scope scope = FriendFinder.class.getAnnotation(Scope.class);
    Assert.assertNotNull(scope);
    Assert.assertEquals(ScopedProxyMode.INTERFACES, scope.proxyMode());
    TestCase.assertEquals(ConfigurableBeanFactory.SCOPE_PROTOTYPE, scope.value());
  }

  @Test
  public void testSumOfSourceBasedFindersV2Masking() {
    SourceBasedFriendFinder f1 = mock(SourceBasedFriendFinder.class);
    when(f1.handlesSource(isA(String.class))).thenReturn(true);
    HashMap<String, Set<? extends Object>> map = new HashMap<>();
    map.put(SourceBasedFriendFinder.FRIENDS_KEY, new HashSet<>(Arrays.asList(PONE, PTWO)));
    map.put("Y", new HashSet<>(Collections.singletonList("Yes")));
    when(f1.findFriends(PFOUR)).thenReturn(map);
    SourceBasedFriendFinder f2 = mock(SourceBasedFriendFinder.class);
    when(f2.handlesSource(isA(String.class))).thenReturn(true);
    HashMap<String, Set<? extends Object>> map1 = new HashMap<>();
    map1.put(SourceBasedFriendFinder.FRIENDS_KEY, new HashSet<>(Arrays.asList(PONE, PTHREE)));
    map1.put("X", new HashSet<>(Arrays.asList(1, 2, 3)));
    when(f2.findFriends(PFOUR)).thenReturn(map1);
    SourceBasedFriendFinder f3 = mock(SourceBasedFriendFinder.class);
    when(f3.handlesSource(isA(String.class))).thenReturn(false);

    AbstractPlayerRepository repository = mock(AbstractPlayerRepository.class);
    when(repository.findById(PFOUR.getId())).thenReturn(Optional.of(PFOUR));
    Map<String, String> map2 = new HashMap<>();
    map2.put("md5", "x");
    map2.put("displayName", "y");
    Map<String, String> map3 = new HashMap<>();
    map3.put("id", "1");
    map3.put("displayName", "2");
    List<Map<String, String>> masked = Arrays.asList(map2, map3);
    PlayerMasker masker = mock(PlayerMasker.class);
    when(masker.maskFriendsV2(eq(new HashSet(Arrays.asList(PONE, PTWO, PTHREE)))))
        .thenReturn(masked);
    finder = new FriendFinder(repository, Arrays.asList(f1, f2, f3), masker);

    Map<String, Set<? super Object>> results = finder.findFriendsV2(PFOUR.getId());
    TestCase.assertEquals(3, results.size());
    Assert.assertEquals(new HashSet<>(Collections.singletonList("Yes")), results.get("Y"));
    Assert.assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), results.get("X"));
    Assert.assertEquals(new HashSet<>(masked),
        results.get(SourceBasedFriendFinder.MASKED_FRIENDS_KEY));
  }

  @Test
  public void testEmptyMaskedFriendsIfNoFriendsV2() {
    SourceBasedFriendFinder f1 = mock(SourceBasedFriendFinder.class);
    when(f1.handlesSource(isA(String.class))).thenReturn(true);
    when(f1.findFriends(PFOUR)).thenReturn(Collections.emptyMap());

    AbstractPlayerRepository repository = mock(AbstractPlayerRepository.class);
    when(repository.findById(PFOUR.getId())).thenReturn(Optional.of(PFOUR));
    finder = new FriendFinder(repository, Collections.singletonList(f1), null);

    Map<String, Set> map = new HashMap<>();
    map.put(SourceBasedFriendFinder.MASKED_FRIENDS_KEY, new HashSet<>());
    Assert.assertEquals(map, finder.findFriendsV2(PFOUR.getId()));
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testNoPlayerInRepositoryV2() {
    AbstractPlayerRepository repository = mock(AbstractPlayerRepository.class);
    when(repository.findById(PFOUR.getId())).thenReturn(Optional.empty());
    finder = new FriendFinder(repository, new ArrayList<>(), null);

    finder.findFriendsV2(PFOUR.getId());
  }

  @Test(expected = FailedToFindPlayersException.class)
  public void testDisabledPlayerV2() {
    AbstractPlayerRepository repository = mock(AbstractPlayerRepository.class);
    when(repository.findById(PINACTIVE1.getId())).thenReturn(Optional.of(PINACTIVE1));
    finder = new FriendFinder(repository, new ArrayList<>(), null);

    finder.findFriendsV2(PINACTIVE1.getId());
  }
}
