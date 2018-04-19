package com.jtbdevelopment.games.dictionary;

import java.util.Set;

/**
 * Date: 12/30/2014 Time: 8:23 PM
 */
public interface Dictionary {

  Set<String> words();

  boolean isValidWord(final String input);
}
