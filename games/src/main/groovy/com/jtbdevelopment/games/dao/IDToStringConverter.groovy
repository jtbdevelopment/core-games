package com.jtbdevelopment.games.dao

import org.springframework.core.convert.converter.Converter

/**
 * Date: 3/7/15
 * Time: 2:57 PM
 */
interface IDToStringConverter<ID extends Serializable> extends Converter<ID, String> {
}
