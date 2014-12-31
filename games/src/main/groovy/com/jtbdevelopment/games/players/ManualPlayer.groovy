package com.jtbdevelopment.games.players

import groovy.transform.CompileStatic

/**
 * Date: 12/30/2014
 * Time: 1:50 PM
 */
@CompileStatic
interface ManualPlayer<ID extends Serializable> extends Player<ID> {
    public static final String MANUAL_SOURCE = "MANUAL"

    String getPassword()

    void setPassword(final String password)

    String getVerificationToken()

    void setVerificationToken(final String password)

    boolean isVerified()

    boolean getVerified()

    void setVerified(boolean verified)
}