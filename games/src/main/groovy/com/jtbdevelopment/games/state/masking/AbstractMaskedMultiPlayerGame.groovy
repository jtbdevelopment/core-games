package com.jtbdevelopment.games.state.masking

import com.jtbdevelopment.games.state.PlayerState
import groovy.transform.CompileStatic

/**
 * Date: 2/18/15
 * Time: 6:55 PM
 */
@CompileStatic
abstract class AbstractMaskedMultiPlayerGame<FEATURES> extends AbstractMaskedGame<FEATURES> implements MaskedMultiPlayerGame<FEATURES> {
    String maskedForPlayerID
    String maskedForPlayerMD5

    Long declinedTimestamp
    Long rematchTimestamp

    String initiatingPlayer
    Map<String, PlayerState> playerStates = [:]  // key will be md5 key
}
