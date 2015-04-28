package com.jtbdevelopment.games.rest

import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler
import com.jtbdevelopment.games.rest.handlers.ChallengeToRematchHandler
import com.jtbdevelopment.games.rest.handlers.QuitHandler
import com.jtbdevelopment.games.state.PlayerState
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import groovy.transform.TypeChecked

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Date: 4/8/2015
 * Time: 10:28 PM
 */
class AbstractMultiPlayerGameServicesTest extends GroovyTestCase {
    AbstractMultiPlayerGameServices services = new AbstractMultiPlayerGameServices() {}
    private final AbstractMaskedMultiPlayerGame result = new AbstractMaskedMultiPlayerGame() {}
    private static final String PID = "PID-122"
    private static final String GID = "GID-111354"

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        services.playerID.set(PID)
        services.gameID.set(GID)
    }

    void testActionAnnotations() {

        Map<String, List<Object>> stuff = [
                //  method: [name, params, path, path param values, consumes
                "createRematch": ["rematch", [], [], []],
                "rejectGame"   : ["reject", [], [], []],
                "quitGame"     : ["quit", [], [], []],
                "acceptGame"   : ["accept", [], [], []],
        ]
        stuff.each {
            String method, List<Object> details ->
                def m = AbstractMultiPlayerGameServices.getMethod(method, details[1] as Class[])
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

    void testRejectGame() {
        services.responseHandler = [
                handleAction: {
                    String p, String g, PlayerState r ->
                        assert p == PID
                        assert g == GID
                        assert r == PlayerState.Rejected
                        result
                }
        ] as ChallengeResponseHandler
        assert result.is(services.rejectGame())
    }

    void testAcceptGame() {
        services.responseHandler = [
                handleAction: {
                    String p, String g, PlayerState r ->
                        assert p == PID
                        assert g == GID
                        assert r == PlayerState.Accepted
                        result
                }
        ] as ChallengeResponseHandler
        assert result.is(services.acceptGame())
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

}
