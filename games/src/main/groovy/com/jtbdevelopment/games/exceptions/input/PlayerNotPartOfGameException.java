package com.jtbdevelopment.games.exceptions.input;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 1/13/15 Time: 6:59 PM
 */
public class PlayerNotPartOfGameException extends GameInputException {

  private static final String MESSAGE = "Player trying to act on a game they are not part of.";

  public PlayerNotPartOfGameException() {
    super(MESSAGE);
  }
}
