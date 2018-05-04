package com.jtbdevelopment.games.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Date: 10/27/14 Time: 6:55 PM
 *
 * Dictionary that loads from generated com.jtbdevelopment.games.dictionary.aspell net file
 *
 * As per com.jtbdevelopment.games.dictionary.aspell net copyright:
 *
 * The dictionary file loaded as part of this code and committed in source tree is from apsell.net
 *
 * Copyright 2000-2014 by Kevin Atkinson
 *
 * Permission to use, copy, modify, distribute and sell these word lists, the associated scripts,
 * the output created from the scripts, and its documentation for any purpose is hereby granted
 * without fee, provided that the above copyright notice appears in all copies and that both that
 * copyright notice and this permission notice appear in supporting documentation. Kevin Atkinson
 * makes no representations about the suitability of this array for any purpose. It is provided "as
 * is" without express or implied warranty.
 *
 * Copyright (c) J Ross Beresford 1993-1999. All Rights Reserved.
 *
 * The following restriction is placed on the use of this publication: if The UK Advanced Cryptics
 * Dictionary is used in a software package or redistributed in any form, the copyright notice must
 * be prominently displayed and the text of this document must be included verbatim.
 *
 * There are no other restrictions: I would like to see the list distributed as widely as possible.
 */
public class AspellUSEnglishCaseInsensitiveDictionary implements Dictionary {

  private static final Logger log = LoggerFactory
      .getLogger(AspellUSEnglishCaseInsensitiveDictionary.class);
  private final Set<String> words = new HashSet<>(700000);

  AspellUSEnglishCaseInsensitiveDictionary(final String dictionaryFile) {
    log.info("Loading dictionary..");
    readInWords(dictionaryFile);

    log.info("Loading taboo and offensive words");
    for (String file : Arrays.asList("offensive.1", "offensive.2", "profane.1", "profane.3")) {
      removeOffensiveWords(file);
    }

    log.info("Dictionaries loaded.");
  }

  private void removeOffensiveWords(final String file) {
    log.info("Removing offensive/profane from " + file);
    try (BufferedReader reader = Files.newBufferedReader(
        new ClassPathResource("/aspell/" + file).getFile().toPath())
    ) {
      final AtomicInteger counter = new AtomicInteger();
      reader.lines().forEach(line -> {
        words.remove(line.toUpperCase());
        counter.set(counter.get() + 1);
      });
      log.info("Processed = " + counter.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void readInWords(final String dictionaryFile) {
    try (BufferedReader reader = Files.newBufferedReader(
        new ClassPathResource(dictionaryFile).getFile().toPath())
    ) {
      final AtomicInteger counter = new AtomicInteger();
      final boolean[] foundStart = new boolean[]{false};
      reader.lines().forEach(line -> {
        if (foundStart[0]) {
          words.add(line.toUpperCase());
          if (counter.incrementAndGet() % 50000 == 0) {
            log.info("Processed = " + counter.get());
          }

        } else if (line.equals("---")) {
          log.info("Found dictionary start");
          foundStart[0] = true;
        }
      });
      log.info("Processed = " + counter.get());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Set<String> words() {
    return this.words;
  }

  @Override
  public boolean isValidWord(final String input) {
    return words.contains(input.toUpperCase());
  }

}
