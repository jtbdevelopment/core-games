package com.jtbdevelopment.games.dictionary

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Date: 10/27/14
 * Time: 6:55 PM
 *
 * Dictionary that loads from generated com.jtbdevelopment.games.dictionary.aspell net file
 *
 * As per com.jtbdevelopment.games.dictionary.aspell net copyright:
 *
 * The dictionary file loaded as part of this code and committed in source tree is from apsell.net
 *
 Copyright 2000-2014 by Kevin Atkinson

 Permission to use, copy, modify, distribute and sell these word
 lists, the associated scripts, the output created from the scripts,
 and its documentation for any purpose is hereby granted without fee,
 provided that the above copyright notice appears in all copies and
 that both that copyright notice and this permission notice appear in
 supporting documentation. Kevin Atkinson makes no representations
 about the suitability of this array for any purpose. It is provided
 "as is" without express or implied warranty.

 Copyright (c) J Ross Beresford 1993-1999. All Rights Reserved.

 The following restriction is placed on the use of this publication:
 if The UK Advanced Cryptics Dictionary is used in a software package
 or redistributed in any form, the copyright notice must be
 prominently displayed and the text of this document must be included
 verbatim.

 There are no other restrictions: I would like to see the list
 distributed as widely as possible.

 */

@CompileStatic
@Component
class AspellUSEnglishCaseInsensitiveDictionary implements Dictionary {
    private static final Logger log = LoggerFactory.getLogger(AspellUSEnglishCaseInsensitiveDictionary.class)

    private final Set<String> words = new HashSet<>(700000);

    public AspellUSEnglishCaseInsensitiveDictionary() {
        boolean hitWords = false
        int counter = 0
        log.info("Loading dictionary..")
        InputStream stream = new BufferedInputStream(AspellUSEnglishCaseInsensitiveDictionary.class.getResourceAsStream("/aspell/dictionary.txt"))
        stream.eachLine {
            String line ->
                if (hitWords) {
                    words.add(line.toUpperCase())
                    counter++
                    if (counter % 50000 == 0) {
                        log.info("Processed = " + counter)
                    }
                } else if (line == "---") {
                    log.info("Found dictionary start")
                    hitWords = true
                }
        }
        log.info("Processed = " + counter)
        stream.close()
        log.info("Loading taboo and offensive words")
        for (String file in ['offensive.1', "offensive.2", "profane.1", "profane.3"]) {
            counter = 0
            log.info("Removing offensive/profane from " + file)
            stream = new BufferedInputStream(AspellUSEnglishCaseInsensitiveDictionary.class.getResourceAsStream("/aspell/" + file))
            stream.eachLine {
                String line ->
                    words.remove(line.toUpperCase())
                    counter++
            }
            log.info("Processed = " + counter)
            stream.close()
        }
        log.info("Dictionaries loaded.")
    }

    Set<String> words() {
        return this.words
    }

    @Override
    boolean isValidWord(final String input) {
        return words.contains(input.toUpperCase())
    }
}
