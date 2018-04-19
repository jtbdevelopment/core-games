package com.jtbdevelopment.games.rest.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 1/13/15 Time: 6:53 PM
 */
public class GameIsNotPossibleToQuitNowException extends GameInputException {

  private static final String ERROR = "Game is not available to quit anymore.";

  public GameIsNotPossibleToQuitNowException() {
    super(ERROR);
  }
}
