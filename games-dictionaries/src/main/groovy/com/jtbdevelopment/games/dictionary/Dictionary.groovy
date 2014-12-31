package com.jtbdevelopment.games.dictionary

import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 8:23 PM
 */
@CompileStatic
interface Dictionary {
    boolean isValidWord(final String input);
}