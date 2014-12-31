package com.jtbdevelopment.games.mongo.dao.converters

import groovy.transform.CompileStatic
import org.springframework.core.convert.converter.Converter

/**
 * Date: 12/30/2014
 * Time: 1:07 PM
 *
 * Marker Interface
 */
@CompileStatic
interface MongoConverter<S, T> extends Converter<S, T> {
}
