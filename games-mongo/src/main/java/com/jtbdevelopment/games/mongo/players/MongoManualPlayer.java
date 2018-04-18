package com.jtbdevelopment.games.mongo.players;

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import com.jtbdevelopment.games.players.ManualPlayer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 12/16/14 Time: 6:49 AM
 *
 * For direct login users
 *
 * Username goes in sourceId which should be an email address displayName might default to display
 * name
 */
@Document(collection = "player")
public class MongoManualPlayer extends MongoPlayer implements ManualPlayer<ObjectId> {

  private String password;
  private boolean verified = false;
  private String verificationToken = "";

  public MongoManualPlayer() {
    super.setSource(MANUAL_SOURCE);
  }

  @PersistenceConstructor
  public MongoManualPlayer(final GameSpecificPlayerAttributes gameSpecificPlayerAttributes) {
    super(gameSpecificPlayerAttributes);
    super.setSource(MANUAL_SOURCE);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public String getVerificationToken() {
    return verificationToken;
  }

  public void setVerificationToken(String verificationToken) {
    this.verificationToken = verificationToken;
  }
}
