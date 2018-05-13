package com.jtbdevelopment.games.players;

import com.jtbdevelopment.games.GameCoreTestCase;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 11/26/14 Time: 9:00 PM
 */
public class PlayerMaskerTest {

  private PlayerMasker masker = new PlayerMasker();

  @Test
  public void testMaskFriendsV2() {
    Map<String, String> map = new LinkedHashMap<String, String>();
    map.put("md5", GameCoreTestCase.PONE.getMd5());
    map.put("displayName", GameCoreTestCase.PONE.getDisplayName());
    Map<String, String> map1 = new LinkedHashMap<String, String>();
    map1.put("md5", GameCoreTestCase.PTWO.getMd5());
    map1.put("displayName", GameCoreTestCase.PTWO.getDisplayName());
    Map<String, String> map2 = new LinkedHashMap<String, String>();
    map2.put("md5", GameCoreTestCase.PTHREE.getMd5());
    map2.put("displayName", GameCoreTestCase.PTHREE.getDisplayName());
    Assert.assertEquals(
        Arrays.asList(map, map1, map2),
        masker.maskFriendsV2(
            new HashSet<>(Arrays
                .asList(GameCoreTestCase.PONE, GameCoreTestCase.PTWO, GameCoreTestCase.PTHREE))));
  }
}
