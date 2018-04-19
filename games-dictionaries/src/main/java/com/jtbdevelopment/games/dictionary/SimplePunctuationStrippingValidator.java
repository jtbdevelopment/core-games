package com.jtbdevelopment.games.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 10/28/14 Time: 10:04 PM
 */
@Component
public class SimplePunctuationStrippingValidator implements Validator {

  private static Logger log = LoggerFactory.getLogger(SimplePunctuationStrippingValidator.class);
  @Autowired
  protected DictionaryManager dictionaryManager;

  private static String fixUpInputString(final String wordPhrase) {
    final StringBuilder builder = new StringBuilder();
    for (char c : wordPhrase.toCharArray()) {
      if (Character.isAlphabetic(c) || '\'' == c) {
        builder.append(c);
      } else {
        builder.append(' ');
      }
    }
    String working = builder.toString().trim();

    while (
        working.length() > 0 &&
            working.charAt(working.length() - 1) == '\'') {
      working = working.substring(0, working.length() - 1);
    }

    return working;
  }

  @Override
  public List<String> validateWordPhrase(final String wordPhrase, DictionaryType dictionaryType) {
    final Dictionary dictionary = dictionaryManager.getDictionary(dictionaryType);
    if (wordPhrase == null) {
      log.info("Invalidating null word phrase " + wordPhrase);
      return Arrays.asList("");
    }

    String working = fixUpInputString(wordPhrase);
    if (working.isEmpty()) {
      log.info("Invalidating word phrase as empty " + wordPhrase);
      return Arrays.asList(wordPhrase);
    }

    StringTokenizer tokenizer = new StringTokenizer(working);

    List<String> invalidWords = new ArrayList<>();
    while (tokenizer.hasMoreElements()) {
      String word = tokenizer.nextToken();
      while (word.endsWith("\'")) {
        word = word.substring(0, word.length() - 1);
      }

      if (!dictionary.isValidWord(word)) {
        invalidWords.add(word);
      }
    }

    if (!invalidWords.isEmpty()) {
      log.info("Invalidating word phrase {} for invalid words - ", wordPhrase,
          String.join(",", invalidWords));
    }
    return invalidWords;
  }
}
