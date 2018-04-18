package com.jtbdevelopment.games.mongo.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 11/3/14 Time: 6:53 AM
 */
@Document(collection = "player")
@JsonIgnoreProperties({"idAsString", "sourceAndSourceId"})
@CompoundIndexes({@CompoundIndex(name = "created", def = "{'created': 1}"),
    @CompoundIndex(name = "lastLogin", def = "{'lastLogin': 1}"),
    @CompoundIndex(name = "displayName", def = "{'displayName': 1}"),
    @CompoundIndex(unique = true, name = "id_source", def = "{'sourceId':1, 'source':1}")})
public class MongoPlayer extends AbstractPlayer<ObjectId> implements Cloneable {

  @Id
  private ObjectId id = new ObjectId();
  @Indexed
  private String md5;

  public MongoPlayer() {
  }

  @PersistenceConstructor
  public MongoPlayer(final GameSpecificPlayerAttributes gameSpecificPlayerAttributes) {
    setGameSpecificPlayerAttributes(gameSpecificPlayerAttributes);
  }

  public String getIdAsString() {
    return id != null ? id.toHexString() : null;
  }

  protected String getMd5Internal() {
    return this.md5;
  }

  protected void setMd5(final String md5) {
    this.md5 = md5;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(final ObjectId id) {
    this.id = id;
    computeMD5Hex();
  }
}
