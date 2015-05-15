package com.jtbdevelopment.games.rest.exceptions

import com.jtbdevelopment.games.exceptions.GameSystemException
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Date: 11/13/14
 * Time: 6:54 AM
 */
@Provider
@CompileStatic
class GameSystemExceptionMapper implements ExceptionMapper<GameSystemException> {
    private static final Logger logger = LoggerFactory.getLogger(GameSystemExceptionMapper.class)

    @Override
    Response toResponse(final GameSystemException e) {
        logger.info("Mapping error " + e.message)
        return Response.status(Response.Status.NOT_FOUND)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
