package com.jtbdevelopment.games.rest.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException
import groovy.transform.CompileStatic

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

/**
 * Date: 11/13/14
 * Time: 7:02 AM
 */
@Provider
@CompileStatic
class GameInputExceptionMapper implements ExceptionMapper<GameInputException> {
    @Override
    Response toResponse(final GameInputException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
