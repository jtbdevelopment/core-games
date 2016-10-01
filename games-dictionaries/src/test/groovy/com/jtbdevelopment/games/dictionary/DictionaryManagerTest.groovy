package com.jtbdevelopment.games.dictionary

/**
 * Date: 10/1/2016
 * Time: 12:47 PM
 */
class DictionaryManagerTest extends GroovyTestCase {
    DictionaryManager manager = new DictionaryManager()

    void testGetDictionaryAndGetsItOnce() {
        DictionaryType.values().each {
            Dictionary d = manager.getDictionary(it)
            assertNotNull d
            assert d.isValidWord("friend")
            Dictionary again = manager.getDictionary(it)
            assert d.is(again)
        }
    }
}
