package com.jtbdevelopment.games.player.tracking

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes
import groovy.transform.CompileStatic

/**
 * Date: 1/30/15
 * Time: 6:34 PM
 */
@CompileStatic
abstract class AbstractPlayerGameTrackingAttributes implements GameSpecificPlayerAttributes {
    public static final String FREE_GAMES_FIELD = 'gameSpecificPlayerAttributes.freeGamesUsedToday'
    public static final String PAID_GAMES_FIELD = 'gameSpecificPlayerAttributes.availablePurchasedGames'

    int freeGamesUsedToday = 0
    int availablePurchasedGames = 0
}
