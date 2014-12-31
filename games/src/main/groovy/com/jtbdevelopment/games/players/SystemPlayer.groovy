package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 1:50 PM
 */
@CompileStatic
interface SystemPlayer<ID extends Serializable> extends Player<ID> {
    public static final String SYSTEM_SOURCE = "SOURCE"
}