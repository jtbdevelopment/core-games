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

    public void testLoadPlayerMD5() {
        handler.playerRepository = [
                findByMd5: {
                    String it ->
                        assert it == PTHREE.md5
                        return PTHREE
                }
        ] as AbstractPlayerRepository
        assert PTHREE.is(handler.loadPlayerMD5(PTHREE.md5))
    }

    public void testLoadPlayerMD5Fails() {
        handler.playerRepository = [
                findByMd5: {
                    String it ->
                        assert it == PTHREE.md5
                        return null
                }
        ] as AbstractPlayerRepository
        shouldFail(FailedToFindPlayersException.class, {
            handler.loadPlayerMD5(PTHREE.md5)
        })
    }

    public void testLoadPlayerMD5s() {
        handler.playerRepository = [
                findByMd5In: {
                    Collection<String> it ->
                        assert it as List == [PTWO.md5, PFOUR.md5, PTHREE.md5]
                        return [PTWO, PTHREE, PFOUR]
                }
        ] as AbstractPlayerRepository
        assert handler.loadPlayerMD5s([PTWO.md5, PFOUR.md5, PTHREE.md5]) == [PTWO, PTHREE, PFOUR] as Set
    }

    public void testLoadPlayerMD5sWithPartialLoad() {
        handler.playerRepository = [
                findByMd5In: {
                    Collection<String> it ->
                        assert it as List == [PTWO.md5, PFOUR.md5, PTHREE.md5]
                        return [PTWO, PFOUR]
                }
        ] as AbstractPlayerRepository
        shouldFail(FailedToFindPlayersException.class, {
            handler.loadPlayerMD5s([PTWO.md5, PFOUR.md5, PTHREE.md5])

        })
    }

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
