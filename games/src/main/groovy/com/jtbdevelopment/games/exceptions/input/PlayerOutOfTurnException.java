package com.jtbdevelopment.games.exceptions.input;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 1/13/15 Time: 7:06 PM
 */
public class PlayerOutOfTurnException extends GameInputException {

  private static final String ERROR = "Player is playing out of turn.";

  public PlayerOutOfTurnException() {
    super(ERROR);
  }
}
