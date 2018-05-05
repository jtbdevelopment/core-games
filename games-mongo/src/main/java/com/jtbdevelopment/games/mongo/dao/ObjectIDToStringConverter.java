package com.jtbdevelopment.games.mongo.dao;

import com.jtbdevelopment.games.dao.IDToStringConverter;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
public class ObjectIDToStringConverter implements IDToStringConverter<ObjectId> {

  @Override
  public String convert(final ObjectId source) {
    //noinspection ConstantConditions
    return source != null ? source.toHexString() : null;
  }

}
