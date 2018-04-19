package com.jtbdevelopment.games.dictionary;

/**
 * Date: 10/1/2016 Time: 12:43 PM
 */
public enum DictionaryType {
  USEnglishMaximum("/aspell/maximum-en-us-dictionary.txt"),
  USEnglishModerate("/aspell/moderate-en-us-dictionary.txt"),
  USEnglishSimple("/aspell/simple-en-us-dictionary.txt");

  private final String resourceFile;

  DictionaryType(final String resourceFile) {
    this.resourceFile = resourceFile;
  }

  public final String getResourceFile() {
    return resourceFile;
  }
}
