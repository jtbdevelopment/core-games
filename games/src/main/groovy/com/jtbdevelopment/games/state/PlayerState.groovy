package com.jtbdevelopment.games.state

import groovy.transform.CompileStatic

/**
 * Date: 11/14/14
 * Time: 12:34 PM
 */
@CompileStatic
enum PlayerState {
    Pending,
    Accepted,
    Rejected,
    Quit
}