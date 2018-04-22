package com.jtbdevelopment.games.stringimpl;

import com.jtbdevelopment.games.dao.StringToIDConverter;

/**
 * Date: 4/22/18 Time: 4:01 PM
 */
public class StringToStringConverter implements StringToIDConverter<String> {

  @Override
  public String convert(final String source) {
    if (source == null) {
      return source;
    }
    return new StringBuilder(source).reverse().toString();
  }

}
