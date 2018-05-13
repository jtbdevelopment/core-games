package com.jtbdevelopment.games.dictionary;

import java.util.List;

/**
 * Date: 12/30/2014 Time: 8:23 PM
 */
public interface Validator {

  List<String> validateWordPhrase(final String wordPhrase, final DictionaryType dictionary);
}
