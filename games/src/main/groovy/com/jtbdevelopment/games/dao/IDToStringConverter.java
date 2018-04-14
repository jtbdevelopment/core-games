package com.jtbdevelopment.games.dao;

import java.io.Serializable;
import org.springframework.core.convert.converter.Converter;

/**
 * Date: 3/7/15 Time: 2:57 PM
 */
public interface IDToStringConverter<ID extends Serializable> extends Converter<ID, String> {

}
