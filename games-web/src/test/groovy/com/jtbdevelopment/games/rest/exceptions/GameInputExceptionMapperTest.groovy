package com.jtbdevelopment.games.rest.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Date: 11/13/14
 * Time: 7:09 AM
 */
class GameInputExceptionMapperTest extends GroovyTestCase {
    GameInputExceptionMapper mapper = new GameInputExceptionMapper()

    void testToResponse() {
        String s = "A MESSAGE"
        Response response = mapper.toResponse(new GameInputException(s) {})
        assert response.entity == s
        assert response.status == Response.Status.CONFLICT.statusCode
        assert response.mediaType == MediaType.TEXT_PLAIN_TYPE
    }
}
