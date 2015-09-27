package com.jtbdevelopment.games.dev.utilities.integrationtesting

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.jtbdevelopment.core.mongo.spring.AbstractMongoIntegration
import com.jtbdevelopment.games.dev.utilities.jetty.JettyServer
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository
import com.jtbdevelopment.games.mongo.players.MongoManualPlayer
import com.jtbdevelopment.games.mongo.players.MongoPlayerFactory
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder
import com.jtbdevelopment.games.rest.services.AbstractPlayerGatewayService
import com.jtbdevelopment.games.state.Game
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.eclipse.jetty.server.Server
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.password.PasswordEncoder

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriBuilder

/**
 * Date: 11/15/2014
 * Time: 3:29 PM
 */
@CompileStatic
abstract class AbstractGameIntegration<G extends Game> extends AbstractMongoIntegration {
    protected static final Entity EMPTY_PUT_POST = Entity.entity("", MediaType.TEXT_PLAIN)

    private static Server SERVER;
    private static final int port = 8998;
    static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(port).build();
    static final URI API_URI = BASE_URI.resolve("/api")
    static final URI PLAYER_API = BASE_URI.resolve("api/player")

    static MongoManualPlayer TEST_PLAYER1
    static MongoManualPlayer TEST_PLAYER2
    static MongoManualPlayer TEST_PLAYER3

    static MongoManualPlayer createPlayer(final String id, final String sourceId, final String displayName) {
        MongoPlayerFactory factory = applicationContext.getBean(MongoPlayerFactory.class)
        MongoManualPlayer player = (MongoManualPlayer) factory.newManualPlayer();
        player.id = new ObjectId(id.padRight(24, "0"))
        player.sourceId = sourceId
        player.password = passwordEncoder.encode(sourceId)
        player.displayName = displayName
        player.disabled = false
        player.verified = true
        return player
    }

    abstract Class<G> returnedGameClass();

    static ApplicationContext applicationContext
    static PasswordEncoder passwordEncoder
    static MongoPlayerRepository playerRepository

    @BeforeClass
    public static void initialize() {
        SERVER = JettyServer.makeServer(port, "spring-context-integration.xml")
        SERVER.start()

        assert applicationContext != null
        playerRepository = applicationContext.getBean(MongoPlayerRepository.class)
        passwordEncoder = applicationContext.getBean(PasswordEncoder.class)

        TEST_PLAYER1 = createPlayer("f1234", "ITP1", "TEST PLAYER1")
        TEST_PLAYER2 = createPlayer("f2345", "ITP2", "TEST PLAYER2")
        TEST_PLAYER3 = createPlayer("f3456", "ITP3", "TEST PLAYER3")

        playerRepository.delete(TEST_PLAYER1)
        playerRepository.delete(TEST_PLAYER2)
        playerRepository.delete(TEST_PLAYER3)

        TEST_PLAYER1 = (MongoManualPlayer) playerRepository.save(TEST_PLAYER1)
        TEST_PLAYER2 = (MongoManualPlayer) playerRepository.save(TEST_PLAYER2)
        TEST_PLAYER3 = (MongoManualPlayer) playerRepository.save(TEST_PLAYER3)
    }

    @AfterClass
    public static void tearDown() {
        SERVER.stop()
    }

    @Test
    void testPing() {
        Client client = createConnection(TEST_PLAYER1)
        String response = client
                .target(API_URI)
                .path("ping")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class)
        assert AbstractPlayerGatewayService.PING_RESULT == response
    }

    @Test
    void testGetCurrentPlayer() {
        Client client = createConnection(TEST_PLAYER1)
        def p = client.target(PLAYER_API).request(MediaType.APPLICATION_JSON).get(MongoManualPlayer.class);
        assert p.id == TEST_PLAYER1.id
        assert p.disabled == TEST_PLAYER1.disabled
        assert p.displayName == TEST_PLAYER1.displayName
        assert p.md5 == TEST_PLAYER1.md5
    }

    @Test
    void testGetFriends() {
        Client client = createConnection(TEST_PLAYER1)
        WebTarget path = client
                .target(PLAYER_API)
                .path("friends")
        Map<String, Object> friends = path
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<String, Object>>() {});
        Map<String, String> players = (Map<String, String>) friends[SourceBasedFriendFinder.MASKED_FRIENDS_KEY]
        assert players[TEST_PLAYER2.md5] == TEST_PLAYER2.displayName
        assert players[TEST_PLAYER3.md5] == TEST_PLAYER3.displayName
    }

    @Test
    void testGetPhases() {
        def client = createConnection(TEST_PLAYER3)
        def features = client
                .target(API_URI)
                .path("phases")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<Map<String, List<String>>>() {
        })
        assert features == [
                "Challenged"      : [GamePhase.Challenged.description, GamePhase.Challenged.groupLabel],
                "Declined"        : [GamePhase.Declined.description, GamePhase.Declined.groupLabel],
                "Quit"            : [GamePhase.Quit.description, GamePhase.Quit.groupLabel],
                "Setup"           : [GamePhase.Setup.description, GamePhase.Setup.groupLabel],
                "RoundOver"       : [GamePhase.RoundOver.description, GamePhase.RoundOver.groupLabel],
                "NextRoundStarted": [GamePhase.NextRoundStarted.description, GamePhase.NextRoundStarted.groupLabel],
                "Playing"         : [GamePhase.Playing.description, GamePhase.Playing.groupLabel],
        ]
    }

    protected G getGame(WebTarget target) {
        (G) target.request(MediaType.APPLICATION_JSON).get(returnedGameClass())
    }

    protected G acceptGame(WebTarget target) {
        (G) target.path("accept").request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, returnedGameClass())
    }

    protected G rejectGame(WebTarget target) {
        (G) target.path("reject").request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, returnedGameClass())
    }

    protected G quitGame(WebTarget target) {
        (G) target.path("quit").request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, returnedGameClass())
    }

    protected G rematchGame(WebTarget target) {
        (G) target.path("rematch").request(MediaType.APPLICATION_JSON).put(EMPTY_PUT_POST, returnedGameClass())
    }

    protected static WebTarget createAPITarget(final MongoManualPlayer p) {
        return createConnection(p).target(API_URI)
    }

    protected static WebTarget createPlayerAPITarget(final MongoManualPlayer p) {
        return createConnection(p).target(PLAYER_API)
    }

    protected static WebTarget createGameTarget(final WebTarget source, final Game g) {
        return source.path("game").path(g.idAsString)
    }

    protected static Client createConnection(final MongoManualPlayer p) {
        Client client = ClientBuilder.newClient()
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(p.sourceId, p.sourceId)
        client.register(feature)
        client.register(
                new JacksonJaxbJsonProvider(
                        applicationContext.getBean(ObjectMapper.class),
                        JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS))
        client
    }
}
