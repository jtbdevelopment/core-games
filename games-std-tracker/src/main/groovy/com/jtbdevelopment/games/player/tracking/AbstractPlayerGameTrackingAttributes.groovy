package com.jtbdevelopment.games.player.tracking

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.springframework.data.annotation.Transient

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

    @Transient
    @JsonIgnore
    Player player

    abstract int getMaxDailyFreeGames()

    @SuppressWarnings("GroovyUnusedDeclaration")
    abstract void setMaxDailyFreeGames(int maxDailyFreeGames)
}
