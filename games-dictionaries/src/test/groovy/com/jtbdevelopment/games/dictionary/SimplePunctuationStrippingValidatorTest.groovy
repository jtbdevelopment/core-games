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
            validator.dictionary = new AspellUSEnglishCaseInsensitiveDictionary()
        }
    }


    public void testValidPhrase() {
        assert validator.validateWordPhrase("To be or not to be").size() == 0
    }


    public void testInvalidJunkWord() {
        assert validator.validateWordPhrase("To be or not to bestaffingle") == ["bestaffingle"]
    }


    public void testInvalidOffensiveWord() {
        assert validator.validateWordPhrase("To be or not to wop") == ["wop"]
    }


    public void testInvalidProfaneWord() {
        assert validator.validateWordPhrase("To cunts or not to be") == ["cunts"]
    }


    public void testValidPhraseWithPeriod() {
        assert validator.validateWordPhrase("To be or not to be.").size() == 0
    }


    public void testValidPhraseWithQuestion() {
        assert validator.validateWordPhrase("To be or not to be?").size() == 0
    }


    public void testValidPhraseWithExclamation() {
        assert validator.validateWordPhrase("To be or not to be!").size() == 0
    }


    public void testValidPhraseWithRandom() {
        //  Valid because || will be shown
        assert validator.validateWordPhrase("To be || not to be!").size() == 0
    }


    public void testValidPhraseWithHyphen() {
        assert validator.validateWordPhrase("HOW-TO BREATHE FOR DUMMIES").size() == 0
    }


    public void testValidPossessive() {
        assert validator.validateWordPhrase("GIRLS' NIGHT OUT").size() == 0
    }


    public void testValidPhraseWithEndingPossesive() {
        assert validator.validateWordPhrase("That is Amadeus'").size() == 0
    }


    public void testMultiSentenceWordPhrase() {
        assert validator.validateWordPhrase("To be or not to be.  That is the question.").size() == 0
    }


    public void testExcessSpacingPhrase() {
        assert validator.validateWordPhrase("To  be  or  not   to  be.  That   is   the   question .  ").size() == 0
    }


    public void testInvalidEmptyPhrase() {
        assert validator.validateWordPhrase("") == [""]
    }


    public void testInvalidNullPhrase() {
        assert validator.validateWordPhrase(null) == [""]
    }


    public void testInvalidSpacePhrase() {
        assert validator.validateWordPhrase("  ") == ["  "]
    }


    public void testInvalidPuncationOnlyPhrase() {
        assert validator.validateWordPhrase(" . ") == [" . "]
    }
}
