package com.jtbdevelopment.games.dictionary

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 10/1/2016
 * Time: 12:36 PM
 */
@Component
@CompileStatic
class DictionaryManager {
    private final HashMap<DictionaryType, Dictionary> dictionaries = [:]

    public Dictionary getDictionary(final DictionaryType type) {
        if (dictionaries[type] == null) {
            synchronized (dictionaries) {
                if (dictionaries[type] == null) {
                    dictionaries[type] = new AspellUSEnglishCaseInsensitiveDictionary(type.resourceFile)
                }
            }
        }
        return dictionaries[type]
    }
}
