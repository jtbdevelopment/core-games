package com.jtbdevelopment.games.dictionary

import org.junit.BeforeClass

/**
 * Date: 10/28/14
 * Time: 10:12 PM
 */
class SimplePunctuationStrippingValidatorTest extends GroovyTestCase {
    private static SimplePunctuationStrippingValidator validator;

    @BeforeClass
    public synchronized void setUp() {
        if (!validator) {
            validator = new SimplePunctuationStrippingValidator();
            validator.dictionaryManager = new DictionaryManager()
        }
    }


    public void testValidPhrase() {
        assert validator.validateWordPhrase("To be or not to be", DictionaryType.USEnglishMaximum).size() == 0
    }


    public void testInvalidJunkWord() {
        assert validator.validateWordPhrase("To be or not to bestaffingle", DictionaryType.USEnglishMaximum) == ["bestaffingle"]
    }


    public void testInvalidOffensiveWord() {
        assert validator.validateWordPhrase("To be or not to wop", DictionaryType.USEnglishSimple) == ["wop"]
    }


    public void testInvalidProfaneWord() {
        assert validator.validateWordPhrase("To cunts or not to be", DictionaryType.USEnglishSimple) == ["cunts"]
    }


    public void testValidPhraseWithPeriod() {
        assert validator.validateWordPhrase("To be or not to be.", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testValidPhraseWithQuestion() {
        assert validator.validateWordPhrase("To be or not to be?", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testValidPhraseWithExclamation() {
        assert validator.validateWordPhrase("To be or not to be!", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testValidPhraseWithRandom() {
        //  Valid because || will be shown
        assert validator.validateWordPhrase("To be || not to be!", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testValidPhraseWithHyphen() {
        assert validator.validateWordPhrase("HOW-TO BREATHE FOR DUMMIES", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testValidPossessive() {
        assert validator.validateWordPhrase("GIRLS' NIGHT OUT", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testValidPhraseWithEndingPossesive() {
        assert validator.validateWordPhrase("That is Amadeus'", DictionaryType.USEnglishMaximum).size() == 0
    }


    public void testMultiSentenceWordPhrase() {
        assert validator.validateWordPhrase("To be or not to be.  That is the question.", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testExcessSpacingPhrase() {
        assert validator.validateWordPhrase("To  be  or  not   to  be.  That   is   the   question .  ", DictionaryType.USEnglishSimple).size() == 0
    }


    public void testInvalidEmptyPhrase() {
        assert validator.validateWordPhrase("", DictionaryType.USEnglishSimple) == [""]
    }


    public void testInvalidNullPhrase() {
        assert validator.validateWordPhrase(null, DictionaryType.USEnglishSimple) == [""]
    }


    public void testInvalidSpacePhrase() {
        assert validator.validateWordPhrase("  ", DictionaryType.USEnglishSimple) == ["  "]
    }


    public void testInvalidPunctuationOnlyPhrase() {
        assert validator.validateWordPhrase(" . ", DictionaryType.USEnglishSimple) == [" . "]
    }
}
