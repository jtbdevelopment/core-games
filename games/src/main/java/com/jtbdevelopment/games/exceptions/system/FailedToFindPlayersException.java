package com.jtbdevelopment.games.exceptions.system;

import com.jtbdevelopment.games.exceptions.GameSystemException;

/**
 * Date: 12/30/2014 Time: 1:15 PM
 */
public class FailedToFindPlayersException extends GameSystemException {

  private static final String VALID_PLAYERS = "Not all players in this game are valid anymore.";

  public FailedToFindPlayersException() {
    super(VALID_PLAYERS);
  }
}
