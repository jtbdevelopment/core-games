package com.jtbdevelopment.games.mongo.state.utility;

import com.jtbdevelopment.games.mongo.state.AbstractMongoMultiPlayerGame;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Date: 1/10/15 Time: 2:40 PM
 */
@Document(collection = "multi")
public class SimpleMultiPlayerGame extends AbstractMongoMultiPlayerGame<String> {

    @Indexed
    private int intValue;
    private String stringValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
