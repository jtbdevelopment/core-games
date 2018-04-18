package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.dao.StringToIDConverter;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
public class StringToObjectIdConverter implements StringToIDConverter<ObjectId> {

  @Override
  public ObjectId convert(final String source) {
    return source != null ? new ObjectId(source) : null;
  }

}
