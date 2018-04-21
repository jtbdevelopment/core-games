package com.jtbdevelopment.games;

import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;

/**
 * Date: 11/8/14 Time: 9:09 AM
 */
public class StringMPGame extends AbstractMultiPlayerGame<String, Object> implements Cloneable {

  private String id;
  private String previousId;

  @Override
  public String getIdAsString() {
    return id;
  }

  @Override
  public String getPreviousIdAsString() {
    return previousId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPreviousId() {
    return previousId;
  }

  public void setPreviousId(String previousId) {
    this.previousId = previousId;
  }
}
