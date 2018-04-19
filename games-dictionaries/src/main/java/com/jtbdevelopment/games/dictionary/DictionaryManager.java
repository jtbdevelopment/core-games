package com.jtbdevelopment.games.dictionary;

import java.util.HashMap;
import org.springframework.stereotype.Component;

/**
 * Date: 10/1/2016 Time: 12:36 PM
 */
@Component
public class DictionaryManager {

  private final HashMap<DictionaryType, Dictionary> dictionaries = new HashMap<>();

  public Dictionary getDictionary(final DictionaryType type) {
    if (dictionaries.get(type) == null) {
      synchronized (dictionaries) {
        if (dictionaries.get(type) == null) {
          dictionaries.put(
              type,
              new AspellUSEnglishCaseInsensitiveDictionary(type.getResourceFile()));
        }

      }

    }

    return dictionaries.get(type);
  }
}
