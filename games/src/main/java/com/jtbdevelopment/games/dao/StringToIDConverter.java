package com.jtbdevelopment.games.dao;

import java.io.Serializable;
import org.springframework.core.convert.converter.Converter;

/**
 * Date: 3/7/15 Time: 2:58 PM
 */
public interface StringToIDConverter<ID extends Serializable> extends Converter<String, ID> {

}
