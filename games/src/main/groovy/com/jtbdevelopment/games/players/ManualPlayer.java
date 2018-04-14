package com.jtbdevelopment.games.players;

import java.io.Serializable;

/**
 * Date: 12/30/2014 Time: 1:50 PM
 */
public interface ManualPlayer<ID extends Serializable> extends Player<ID> {

    String MANUAL_SOURCE = "MANUAL";

    String getPassword();

    void setPassword(final String password);

    String getVerificationToken();

    void setVerificationToken(final String password);

    boolean isVerified();

    boolean getVerified();

    void setVerified(boolean verified);
}
