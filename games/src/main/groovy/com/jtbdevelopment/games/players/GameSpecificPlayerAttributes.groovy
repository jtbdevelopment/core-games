package com.jtbdevelopment.games.players

import java.beans.Transient

/**
 * Date: 1/30/15
 * Time: 6:56 PM
 */
interface GameSpecificPlayerAttributes extends Serializable {
    @Transient
    Player getPlayer()

    @Transient
    void setPlayer(final Player player)
}