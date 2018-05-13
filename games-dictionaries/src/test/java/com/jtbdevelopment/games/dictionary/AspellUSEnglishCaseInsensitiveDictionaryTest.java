package com.jtbdevelopment.games.dictionary;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Date: 10/27/14 Time: 7:01 PM
 */
public class AspellUSEnglishCaseInsensitiveDictionaryTest {

  private static AspellUSEnglishCaseInsensitiveDictionary dictionary;

  @BeforeClass
  public static void setUp() {
    dictionary = new AspellUSEnglishCaseInsensitiveDictionary(
        "/aspell/maximum-en-us-dictionary.txt");
  }

  @Test
  public void testWordsAreAvailable() {
    Assert.assertNotNull(dictionary.words());
    Assert.assertEquals(632299, dictionary.words().size());
  }

  @Test
  public void testLowercaseWord() {
    Assert.assertTrue(dictionary.isValidWord("apple"));
  }

  @Test
  public void testUppercaseWord() {
    Assert.assertTrue(dictionary.isValidWord("APPLE"));
  }

  @Test
  public void testMixedCaseWord() {
    Assert.assertTrue(dictionary.isValidWord("Apple"));
  }

  @Test
  public void testInvalidWord() {
    Assert.assertFalse(dictionary.isValidWord("AppleFudge"));
  }

  @Test
  public void testOffensive1Exclusion() {
    Assert.assertFalse(dictionary.isValidWord("niggering"));
  }

  @Test
  public void testOffensive2Exclusion() {
    Assert.assertFalse(dictionary.isValidWord("KRAUT'S"));
  }

  @Test
  public void testProfane1Exclusion() {
    Assert.assertFalse(dictionary.isValidWord("sHittY"));
  }

  @Test
  public void testProfane3Exclusion() {
    Assert.assertFalse(dictionary.isValidWord("cunt"));
  }
}
