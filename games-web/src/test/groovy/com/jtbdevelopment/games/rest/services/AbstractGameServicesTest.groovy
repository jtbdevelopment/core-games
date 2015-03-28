package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.GameCoreTestCase
import com.jtbdevelopment.games.rest.handlers.GameGetterHandler
import com.jtbdevelopment.games.state.masking.AbstractMaskedMultiPlayerGame
import groovy.transform.TypeChecked

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
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

}
