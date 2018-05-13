package com.jtbdevelopment.games.security.spring.social.facebook;

import static com.jtbdevelopment.games.GameCoreTestCase.PFOUR;
import static com.jtbdevelopment.games.GameCoreTestCase.PINACTIVE1;
import static com.jtbdevelopment.games.GameCoreTestCase.PONE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTHREE;
import static com.jtbdevelopment.games.GameCoreTestCase.PTWO;
import static org.mockito.Mockito.when;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.players.ManualPlayer;
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FriendOperations;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Reference;

/**
 * Date: 12/24/14 Time: 2:51 PM
 */
public class FacebookFriendFinderTest {

  private Facebook facebook = Mockito.mock(Facebook.class);
  private AbstractPlayerRepository playerRepository = Mockito.mock(AbstractPlayerRepository.class);
  private FacebookFriendFinder friendFinder = new FacebookFriendFinder(playerRepository, facebook);

  @Test
  public void testHandlesSourceWhenFacebookAvailableAndIsSourceFacebook() {
    Assert.assertFalse(friendFinder.handlesSource(ManualPlayer.MANUAL_SOURCE));
    Assert.assertFalse(friendFinder.handlesSource("twitter"));

    friendFinder = new FacebookFriendFinder(playerRepository, null);
    Assert.assertFalse(friendFinder.handlesSource("facebook"));

    friendFinder = new FacebookFriendFinder(playerRepository, facebook);
    Assert.assertTrue(friendFinder.handlesSource("facebook"));
  }

  @Test
  public void testFindFriends() {
    Reference R1 = new Reference("1");
    Reference R2 = new Reference("2");
    Reference R3 = new Reference("4");
    Reference PTHREE_REF = new Reference(PTHREE.getSourceId());
    FriendOperations operations = Mockito.mock(FriendOperations.class);
    when(facebook.friendOperations()).thenReturn(operations);
    when(operations.getFriends()).thenReturn(new PagedList<>(
        Arrays.asList(
            new Reference(PTWO.getSourceId()),
            new Reference(PFOUR.getSourceId()),
            PTHREE_REF,
            new Reference(PINACTIVE1.getSourceId())),
        new PagingParameters(0, 0, 0L, 0L),
        new PagingParameters(0, 0, 0L, 0L)));
    when(facebook.fetchConnections("me", "invitable_friends", Reference.class))
        .thenReturn(
            new PagedList<>(Arrays.asList(R1, R2, R3),
                new PagingParameters(0, 0, 0L, 0L),
                new PagingParameters(0, 0, 0L, 0L)));
    when(playerRepository.findBySourceAndSourceIdIn("facebook", new ArrayList<>(
        Arrays
            .asList(PTWO.getSourceId(), PFOUR.getSourceId(), PTHREE.getSourceId(),
                PINACTIVE1.getSourceId())))).thenReturn(new ArrayList<>(
        Arrays.asList(PTWO, PFOUR, PINACTIVE1)));// leaving out PTHREE as not found

    Map<String, Set<?>> friends = friendFinder.findFriends(PONE);
    LinkedHashMap<String, Set<? extends Object>> map = new LinkedHashMap<>(
        3);
    map.put(SourceBasedFriendFinder.FRIENDS_KEY,
        new HashSet<>(Arrays.asList(PTWO, PFOUR, PINACTIVE1)));
    map.put(SourceBasedFriendFinder.NOT_FOUND_KEY, new HashSet<>(Arrays.asList(PTHREE_REF)));
    map.put(SourceBasedFriendFinder.INVITABLE_FRIENDS_KEY,
        new HashSet<>(Arrays.asList(R1, R2, R3)));
    Assert.assertEquals(friends, map);
  }
}
