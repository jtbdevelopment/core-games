package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 7:11 PM
 */
@CompileStatic
interface PlayerFactory<ID extends Serializable> {
    Player<ID> newPlayer()

    Player<ID> newManualPlayer()

    Player<ID> newSystemPlayer()
}
