package com.jtbdevelopment.games

import com.jtbdevelopment.games.players.ManualPlayer

/**
 * Date: 11/8/14
 * Time: 9:09 AM
 */
class StringManualPlayer extends StringPlayer implements ManualPlayer<String> {
    String password
    String verificationToken
    boolean verified
}

