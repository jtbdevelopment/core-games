package com.jtbdevelopment.games.rest.handlers

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.exceptions.system.FailedToFindPlayersException

/**
 * Date: 11/10/14
 * Time: 6:56 PM
 */
class AbstractHandlerTest extends GameCoreTestCase {
    private class TestHandler extends AbstractHandler {

    }
    TestHandler handler = new TestHandler()


    public void testLoadPlayer() {
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PTWO.id
                        return PTWO
                }
        ] as AbstractPlayerRepository

        assert PTWO.is(handler.loadPlayer(PTWO.id))
    }


    public void testLoadPlayerFindsNull() {
        handler.playerRepository = [
                findOne: {
                    String it ->
                        assert it == PTWO.id
                        return null
                }
        ] as AbstractPlayerRepository

        shouldFail(FailedToFindPlayersException.class, {
            handler.loadPlayer(PTWO.id)
        })
    }


}
