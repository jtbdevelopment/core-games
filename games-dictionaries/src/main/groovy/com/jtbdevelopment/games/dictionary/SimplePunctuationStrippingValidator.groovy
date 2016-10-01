package com.jtbdevelopment.games.dictionary

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 10/28/14
 * Time: 10:04 PM
 */
@Component
@CompileStatic
class SimplePunctuationStrippingValidator implements Validator {
    private static Logger log = LoggerFactory.getLogger(SimplePunctuationStrippingValidator.class)
    @Autowired
    Dictionary dictionary

    @Override
    List<String> validateWordPhrase(final String wordPhrase) {
        if (wordPhrase == null) {
            log.info("Invalidating null word phrase " + wordPhrase);
            return [""];
        }

        String working = fixUpInputString(wordPhrase)
        if (working.empty) {
            log.info("Invalidating word phrase as empty " + wordPhrase);
            return [wordPhrase]
        }

        Collection<String> invalid = working.tokenize().findAll {
            String word ->
                while (word.endsWith('\'')) {
                    word = word.substring(0, word.length() - 1)
                }
                if (!dictionary.isValidWord(word)) {
                    return true
                }
        }
        if (invalid) {
            log.info("Invalidating word phrase " + wordPhrase + " for " + invalid);
            return invalid.toList()
        }
        return []
    }

    private static String fixUpInputString(final String wordPhrase) {
        StringBuilder builder = new StringBuilder()
        wordPhrase.toCharArray().each {
            char c ->
                if (c.isLetter() || c == '\'' as char) {
                    builder.append(c)
                } else {
                    builder.append(' ')
                }
        }
        String working = builder.toString().trim();

        while (working.length() > 0 && working.charAt(working.length() - 1) == '\'' as char) {
            working = working.substring(0, working.length() - 1)
        }

        return working
    }
}
