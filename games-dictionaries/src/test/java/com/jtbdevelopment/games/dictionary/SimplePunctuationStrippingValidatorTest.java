package com.jtbdevelopment.games.dictionary;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Date: 10/28/14 Time: 10:12 PM
 */
public class SimplePunctuationStrippingValidatorTest {

  private static SimplePunctuationStrippingValidator validator;

  @BeforeClass
  public static void setUp() {
    validator = new SimplePunctuationStrippingValidator();
    validator.dictionaryManager = new DictionaryManager();
  }

  @Test
  public void testValidPhrase() {
    assertEquals(0,
        validator.validateWordPhrase("To be or not to be", DictionaryType.USEnglishMaximum).size());
  }

  @Test
  public void testInvalidJunkWord() {
    assertEquals(Collections.singletonList("bestaffingle"), validator
        .validateWordPhrase("To be or not to bestaffingle", DictionaryType.USEnglishMaximum));
  }

  @Test
  public void testInvalidOffensiveWord() {
    assertEquals(Collections.singletonList("wop"),
        validator.validateWordPhrase("To be or not to wop", DictionaryType.USEnglishSimple));
  }

  @Test
  public void testInvalidProfaneWord() {
    assertEquals(Collections.singletonList("cunts"),
        validator.validateWordPhrase("To cunts or not to be", DictionaryType.USEnglishSimple));
  }

  @Test
  public void testValidPhraseWithPeriod() {
    assertEquals(0,
        validator.validateWordPhrase("To be or not to be.", DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testValidPhraseWithQuestion() {
    assertEquals(0,
        validator.validateWordPhrase("To be or not to be?", DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testValidPhraseWithExclamation() {
    assertEquals(0,
        validator.validateWordPhrase("To be or not to be!", DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testValidPhraseWithRandom() {
    //  Valid because || will be shown
    assertEquals(0,
        validator.validateWordPhrase("To be || not to be!", DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testValidPhraseWithHyphen() {
    assertEquals(0,
        validator.validateWordPhrase("HOW-TO BREATHE FOR DUMMIES", DictionaryType.USEnglishSimple)
            .size());
  }

  @Test
  public void testValidPossessive() {
    assertEquals(0,
        validator.validateWordPhrase("GIRLS' NIGHT OUT", DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testValidPhraseWithEndingPossesive() {
    assertEquals(0,
        validator.validateWordPhrase("That is Amadeus'", DictionaryType.USEnglishMaximum).size());
  }

  @Test
  public void testMultiSentenceWordPhrase() {
    assertEquals(0, validator
        .validateWordPhrase("To be or not to be.  That is the question.",
            DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testExcessSpacingPhrase() {
    assertEquals(0, validator
        .validateWordPhrase("To  be  or  not   to  be.  That   is   the   question .  ",
            DictionaryType.USEnglishSimple).size());
  }

  @Test
  public void testInvalidEmptyPhrase() {
    assertEquals(Collections.singletonList(""),
        validator.validateWordPhrase("", DictionaryType.USEnglishSimple));
  }

  @Test
  public void testInvalidNullPhrase() {
    assertEquals(Collections.singletonList(""),
        validator.validateWordPhrase(null, DictionaryType.USEnglishSimple));
  }

  @Test
  public void testInvalidSpacePhrase() {
    assertEquals(Collections.singletonList("  "),
        validator.validateWordPhrase("  ", DictionaryType.USEnglishSimple));
  }

  @Test
  public void testInvalidPunctuationOnlyPhrase() {
    assertEquals(Collections.singletonList(" . "),
        validator.validateWordPhrase(" . ", DictionaryType.USEnglishSimple));
  }
}
