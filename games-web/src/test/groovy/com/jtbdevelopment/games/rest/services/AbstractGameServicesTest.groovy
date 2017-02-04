package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.rest.handlers.ChallengeToRematchHandler
import com.jtbdevelopment.games.rest.handlers.DeclineRematchOptionHandler
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler
import com.jtbdevelopment.games.rest.handlers.QuitHandler
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import groovy.transform.TypeChecked

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 3/27/15
 * Time: 6:54 PM
 */
class AbstractGameServicesTest extends GameCoreTestCase {
    private static final String PID = "4r3e"
    private static final String GID = "123d"
    private final AbstractMaskedMultiPlayerGame result = new AbstractMaskedMultiPlayerGame() {}
    AbstractGameServices services = new AbstractGameServices() {}

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        services.playerID.set(PID)
        services.gameID.set(GID)
    }

    void testGet() {
        services.gameGetterHandler = [
                getGame: {
                    String p, String g ->
                        assert p == PID
                        assert g == GID
                        result
                }
        ] as GameGetterHandler
        assert result.is(services.getGame())
    }

    void testGetAnnotations() {
        def m = AbstractGameServices.getMethod("getGame", [] as Class[])
        assert (m.annotations.size() == 2 ||
                (m.annotations.size() == (3) && m.isAnnotationPresent(TypeChecked.TypeCheckingInfo.class))
        )
        assert m.isAnnotationPresent(GET.class)
        assert m.isAnnotationPresent(Produces.class)
        assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assertFalse m.isAnnotationPresent(Path.class)
    }

    void testActionAnnotations() {
        Map<String, List<Object>> stuff = [
                //  method: [name, params, path, path param values, consumes
                "endRematch"   : ["endRematch", [], [], []],
                "createRematch": ["rematch", [], [], []],
                "quitGame"     : ["quit", [], [], []],
        ]
        stuff.each {
            String method, List<Object> details ->
                def m = AbstractGameServices.getMethod(method, details[1] as Class[])
                int expectedA = 3 + details[3].size
                assert (m.annotations.size() == expectedA ||
                        (m.annotations.size() == (expectedA + 1) && m.isAnnotationPresent(TypeChecked.TypeCheckingInfo.class))
                )
                assert m.isAnnotationPresent(PUT.class)
                assert m.isAnnotationPresent(Produces.class)
                assert m.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
                assert m.isAnnotationPresent(Path.class)
                assert m.getAnnotation(Path.class).value() == details[0]
                if (details[3].size > 0) {
                    assert m.isAnnotationPresent(Consumes.class)
                    assert m.getAnnotation(Consumes.class).value() == details[3]
                }
                if (details[2].size > 0) {
                    int count = 0
                    details[2].each {
                        String pp ->
                            ((PathParam) m.parameterAnnotations[count][0]).value() == pp
                            ++count
                    }
                }
        }
    }

    void testCreateRematch() {
        services.rematchHandler = [
                handleAction: {
                    String p, String g ->
                        assert p == PID
                        assert g == GID
                        result
                }
        ] as ChallengeToRematchHandler
        assert result.is(services.createRematch())
    }

    void testQuitGame() {
        services.quitHandler = [
                handleAction: {
                    String p, String g ->
                        assert p == PID
                        assert g == GID
                        result
                }
        ] as QuitHandler
        assert result.is(services.quitGame())
    }

    void testEndRematches() {
        services.declineRematchOptionHandler = [
                handleAction: {
                    String p, String g ->
                        assert p == PID
                        assert g == GID
                        result
                }
        ] as DeclineRematchOptionHandler
        assert result.is(services.endRematch())
    }
}
