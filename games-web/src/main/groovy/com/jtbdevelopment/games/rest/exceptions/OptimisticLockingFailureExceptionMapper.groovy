package com.jtbdevelopment.games.rest.exceptions

import groovy.transform.CompileStatic
import org.springframework.dao.OptimisticLockingFailureException

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
class OptimisticLockingFailureExceptionMapper implements ExceptionMapper<OptimisticLockingFailureException> {
    @Override
    Response toResponse(final OptimisticLockingFailureException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(e.message)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build()
    }
}
