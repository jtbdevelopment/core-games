package com.jtbdevelopment.games.rest.exceptions

import com.jtbdevelopment.games.exceptions.GameSystemException

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 11/13/14
 * Time: 7:09 AM
 */
class GameSystemExceptionMapperTest extends GroovyTestCase {
    GameSystemExceptionMapper mapper = new GameSystemExceptionMapper()

    void testToResponse() {
        String s = "A MESSAGE"
        Response response = mapper.toResponse(new GameSystemException(s) {})
        assert response.entity == s
        assert response.status == Response.Status.NOT_FOUND.statusCode
        assert response.mediaType == MediaType.TEXT_PLAIN_TYPE
    }
}
