package com.jtbdevelopment.games.rest;

import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.rest.services.AbstractGameServices;
import com.jtbdevelopment.games.state.AbstractSinglePlayerGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedSinglePlayerGame;
import java.io.Serializable;

/**
 * Date: 4/8/2015 Time: 10:26 PM
 */
@SuppressWarnings("unused")
public abstract class AbstractSinglePlayerGameServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractSinglePlayerGame<ID, FEATURES>,
    M extends AbstractMaskedSinglePlayerGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    extends AbstractGameServices<ID, FEATURES, IMPL, M, P> {

}
