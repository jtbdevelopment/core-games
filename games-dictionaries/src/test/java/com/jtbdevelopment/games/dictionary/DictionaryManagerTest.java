package com.jtbdevelopment.games.dictionary;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 10/1/2016 Time: 12:47 PM
 */
public class DictionaryManagerTest {

  private DictionaryManager manager = new DictionaryManager();

  @Test
  public void testGetDictionaryAndGetsItOnce() {
    Arrays.stream(DictionaryType.values()).forEach(dictionaryType -> {
      Dictionary d = manager.getDictionary(dictionaryType);
      Assert.assertNotNull(d);
      Assert.assertTrue(d.isValidWord("friend"));
      Dictionary again = manager.getDictionary(dictionaryType);
      Assert.assertSame(d, again);
    });
  }
}
