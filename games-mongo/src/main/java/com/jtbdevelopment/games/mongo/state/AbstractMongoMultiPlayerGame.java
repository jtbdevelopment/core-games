package com.jtbdevelopment.games.mongo.state;

import com.jtbdevelopment.games.state.AbstractMultiPlayerGame;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

/**
 * Date: 1/9/15
 * Time: 10:21 PM
 */
@CompoundIndexes({@CompoundIndex(name = "created", def = "{'created': 1}"), @CompoundIndex(name = "lastUpdated", def = "{'lastUpdate': 1}")})
public abstract class AbstractMongoMultiPlayerGame<FEATURES> extends AbstractMultiPlayerGame<ObjectId, FEATURES> {
    @Id
    private ObjectId id;
    private ObjectId previousId;

    @Override
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }

    @Override
    public String getPreviousIdAsString() {
        return previousId != null ? previousId.toHexString() : null;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getPreviousId() {
        return previousId;
    }

    public void setPreviousId(ObjectId previousId) {
        this.previousId = previousId;
    }
}
