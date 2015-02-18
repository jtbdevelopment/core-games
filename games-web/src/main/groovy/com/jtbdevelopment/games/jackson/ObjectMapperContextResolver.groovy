package com.jtbdevelopment.games.jackson

import com.fasterxml.jackson.databind.ObjectMapper
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
    ObjectMapper objectMapper

    @Override
    ObjectMapper getContext(final Class<?> type) {
        return objectMapper
    }
}
