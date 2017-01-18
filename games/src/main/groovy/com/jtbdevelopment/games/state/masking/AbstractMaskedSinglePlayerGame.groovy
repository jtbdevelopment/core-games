package com.jtbdevelopment.games.state.masking

import groovy.transform.CompileStatic

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
@CompileStatic
abstract class AbstractMaskedSinglePlayerGame<FEATURES> extends AbstractMaskedGame<FEATURES> implements MaskedSinglePlayerGame<FEATURES> {
    /*

    int round

    Long declinedTimestamp
    Long rematchTimestamp


    String initiatingPlayer
    Map<String, PlayerState> playerStates = [:]  // key will be md5 key

*/
}
