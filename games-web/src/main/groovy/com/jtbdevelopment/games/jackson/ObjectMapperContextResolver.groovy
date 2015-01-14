package com.jtbdevelopment.games.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.jtbdevelopment.spring.jackson.ObjectMapperFactory
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

/**
 * Date: 1/14/15
 * Time: 6:52 AM
 */
@Provider
class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
    @Autowired
    ObjectMapperFactory objectMapperFactory

    @Override
    ObjectMapper getContext(final Class<?> type) {
        return objectMapperFactory.objectMapper
    }
}
