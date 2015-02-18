package com.jtbdevelopment.games.jackson

import com.fasterxml.jackson.databind.ObjectMapper

import javax.ws.rs.ext.Provider

/**
 * Date: 1/14/15
 * Time: 6:55 AM
 */
class ObjectMapperContextResolverTest extends GroovyTestCase {
    ObjectMapperContextResolver resolver = new ObjectMapperContextResolver()

    void testGetContext() {
        def mapper = [] as ObjectMapper
        resolver.objectMapper = mapper

        assert mapper.is(resolver.getContext(null))
    }

    void testClassAnnotations() {
        assert ObjectMapperContextResolver.class.isAnnotationPresent(Provider.class)
    }
}
