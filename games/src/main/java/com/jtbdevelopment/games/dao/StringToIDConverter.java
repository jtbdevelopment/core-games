package com.jtbdevelopment.games.dao;

import org.springframework.core.convert.converter.Converter;

import java.io.Serializable;

/**
 * Date: 3/7/15
 * Time: 2:58 PM
 */
public interface StringToIDConverter<ID extends Serializable> extends Converter<String, ID> {
}
