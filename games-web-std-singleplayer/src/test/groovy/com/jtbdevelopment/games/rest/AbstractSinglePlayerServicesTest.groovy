package com.jtbdevelopment.games.rest

import com.jtbdevelopment.games.rest.handlers.PlayerGamesFinderHandler
import groovy.transform.TypeChecked

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import static com.jtbdevelopment.games.GameCoreTestCase.makeSimpleMPGame

/**
 * Date: 4/8/2015
 * Time: 10:28 PM
 */
class AbstractSinglePlayerServicesTest extends GroovyTestCase {
    AbstractSinglePlayerServices services = new AbstractSinglePlayerServices() {}
    private static final String PID = "PID-122"

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        services.playerID.set(PID)
    }

    void testGetGames() {
        def APLAYER = PID
        def results = [makeSimpleMPGame("1"), makeSimpleMPGame("2"), makeSimpleMPGame("3")]
        services.playerGamesFinderHandler = [
                findGames: {
                    Serializable it ->
                        assert it == APLAYER
                        return results
                }
        ] as PlayerGamesFinderHandler
        services.playerID.set(APLAYER)
        assert results.is(services.gamesForPlayer())
    }

    void testGamesAnnotations() {
        def gameServices = AbstractSinglePlayerServices.getMethod("gamesForPlayer", [] as Class[])
        assert (gameServices.annotations.size() == 3 ||
                (gameServices.isAnnotationPresent(TypeChecked.TypeCheckingInfo) && gameServices.annotations.size() == 4)
        )
        assert gameServices.isAnnotationPresent(Path.class)
        assert gameServices.getAnnotation(Path.class).value() == "games"
        assert gameServices.isAnnotationPresent(Produces.class)
        assert gameServices.getAnnotation(Produces.class).value() == [MediaType.APPLICATION_JSON]
        assert gameServices.isAnnotationPresent(GET.class)
        def params = gameServices.parameterAnnotations
        assert params.length == 0
    }

}
