package com.jtbdevelopment.games;

import com.jtbdevelopment.games.players.AbstractPlayer;

/**
 * Date: 11/8/14 Time: 9:09 AM
 */
public class StringPlayer extends AbstractPlayer<String> {

  private String md5;
  private String id;

  @Override
  protected void setMd5(final String md5) {
    this.md5 = md5;
  }

  @Override
  protected String getMd5Internal() {
    return this.md5;
  }

  @Override
  public String getIdAsString() {
    return id;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
    computeMD5Hex();
  }
}
