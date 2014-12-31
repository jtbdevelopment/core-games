package com.jtbdevelopment.games.dictionary

import org.junit.BeforeClass

/**
 * Date: 10/27/14
 * Time: 7:01 PM
 */
class AspellUSEnglishCaseInsensitiveDictionaryTest extends GroovyTestCase {
    private static AspellUSEnglishCaseInsensitiveDictionary dictionary;


    @BeforeClass
    public synchronized void setUp() {

        if (!dictionary) {
            dictionary = new AspellUSEnglishCaseInsensitiveDictionary();
        }
    }


    public void testLowercaseWord() {
        assert dictionary.isValidWord("apple")
    }


    public void testUppercaseWord() {
        assert dictionary.isValidWord("APPLE")
    }


    public void testMixedCaseWord() {
        assert dictionary.isValidWord("Apple")
    }


    public void testInvalidWord() {
        assert !dictionary.isValidWord("AppleFudge")
    }


    public void testOffensive1Exclusion() {
        assert !dictionary.isValidWord("niggering")
    }


    public void testOffensive2Exclusion() {
        assert !dictionary.isValidWord("KRAUT'S")
    }


    public void testProfane1Exclusion() {
        assert !dictionary.isValidWord("sHittY")
    }


    public void testProfane3Exclusion() {
        assert !dictionary.isValidWord("cunt")
    }
}
