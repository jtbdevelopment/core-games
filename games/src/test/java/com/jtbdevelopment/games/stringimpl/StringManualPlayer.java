package com.jtbdevelopment.games.stringimpl;

import com.jtbdevelopment.games.players.ManualPlayer;

/**
 * Date: 11/8/14 Time: 9:09 AM
 */
public class StringManualPlayer extends StringPlayer implements ManualPlayer<String> {

  private String password;
  private String verificationToken;
  private boolean verified;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getVerificationToken() {
    return verificationToken;
  }

  public void setVerificationToken(String verificationToken) {
    this.verificationToken = verificationToken;
  }

  public boolean getVerified() {
    return verified;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }
}
