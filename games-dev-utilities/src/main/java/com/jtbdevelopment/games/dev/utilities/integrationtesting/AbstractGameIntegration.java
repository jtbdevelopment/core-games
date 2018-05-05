package com.jtbdevelopment.games.dev.utilities.integrationtesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jtbdevelopment.core.mongo.spring.AbstractMongoDefaultSpringContextIntegration;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dev.utilities.jetty.JettyServer;
import com.jtbdevelopment.games.mongo.dao.MongoPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoManualPlayer;
import com.jtbdevelopment.games.mongo.players.MongoPlayerFactory;
import com.jtbdevelopment.games.players.friendfinder.SourceBasedFriendFinder;
import com.jtbdevelopment.games.rest.services.AbstractPlayerGatewayService;
import com.jtbdevelopment.games.state.Game;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.MultiPlayerGame;
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Date: 11/15/2014
 * Time: 3:29 PM
 *
 * G - internal Game
 * R - returned Game via web calls
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbstractGameIntegration<G extends Game, R extends Game> extends
    AbstractMongoDefaultSpringContextIntegration {

  protected static final Entity EMPTY_PUT_POST = Entity.entity("", MediaType.TEXT_PLAIN);
  private static final int port = 8998;
  private static final URI BASE_URI = UriBuilder.fromUri("http://localhost/").port(port).build();
  private static final URI API_URI = BASE_URI.resolve("/api");
  private static final URI PLAYER_API = BASE_URI.resolve("api/player");
  private static Server SERVER;
  protected static MongoManualPlayer TEST_PLAYER1;
  protected static MongoManualPlayer TEST_PLAYER2;
  protected static MongoManualPlayer TEST_PLAYER3;
  protected static ApplicationContext applicationContext;
  private static PasswordEncoder passwordEncoder;
  private static MongoPlayerRepository playerRepository;

  public static MongoManualPlayer createPlayer(final String id, final String sourceId,
      final String displayName) {
    MongoPlayerFactory factory = applicationContext.getBean(MongoPlayerFactory.class);
    MongoManualPlayer player = (MongoManualPlayer) factory.newManualPlayer();
    player.setId(new ObjectId(StringUtils.leftPad(id, 24, "0")));
    player.setSourceId(sourceId);
    player.setPassword(passwordEncoder.encode(sourceId));
    player.setDisplayName(displayName);
    player.setDisabled(false);
    player.setVerified(true);
    return player;
  }

  @BeforeClass
  public static void initialize() throws Exception {
    SERVER = JettyServer.makeServer(port);
    SERVER.start();

    assert applicationContext != null;
    playerRepository = applicationContext.getBean(MongoPlayerRepository.class);
    passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

    TEST_PLAYER1 = createPlayer("f1234", "ITP1", "TEST PLAYER1");
    TEST_PLAYER2 = createPlayer("f2345", "ITP2", "TEST PLAYER2");
    TEST_PLAYER3 = createPlayer("f3456", "ITP3", "TEST PLAYER3");

    playerRepository.findById(TEST_PLAYER1.getId())
        .ifPresent(mongoPlayer -> playerRepository.deleteById(mongoPlayer.getId()));
    playerRepository.findById(TEST_PLAYER2.getId())
        .ifPresent(mongoPlayer -> playerRepository.deleteById(mongoPlayer.getId()));
    playerRepository.findById(TEST_PLAYER3.getId())
        .ifPresent(mongoPlayer -> playerRepository.deleteById(mongoPlayer.getId()));

    TEST_PLAYER1 = playerRepository.save(TEST_PLAYER1);
    TEST_PLAYER2 = playerRepository.save(TEST_PLAYER2);
    TEST_PLAYER3 = playerRepository.save(TEST_PLAYER3);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    SERVER.stop();
  }

  protected static WebTarget createAPITarget(final MongoManualPlayer p) {
    return createConnection(p).target(API_URI);
  }

  protected static WebTarget createPlayerAPITarget(final MongoManualPlayer p) {
    return createConnection(p).target(PLAYER_API);
  }

  protected static WebTarget createGameTarget(final WebTarget source, final Game g) {
    return source.path("game").path(g.getIdAsString());
  }

  protected static Client createConnection(final MongoManualPlayer p) {
    Client client = ClientBuilder.newClient();
    HttpAuthenticationFeature feature = HttpAuthenticationFeature
        .basic(p.getSourceId(), p.getSourceId());
    client.register(feature);
    client.register(new JacksonJaxbJsonProvider(applicationContext.getBean(ObjectMapper.class),
        JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
    return client;
  }

  public static void setApplicationContext(
      ApplicationContext applicationContext) {
    AbstractGameIntegration.applicationContext = applicationContext;
  }

  protected abstract Class<R> returnedGameClass();

  protected abstract Class<G> internalGameClass();

  protected abstract G newGame();

  protected abstract AbstractGameRepository gameRepository();

  @Test
  public void testPing() {
    Client client = createConnection(TEST_PLAYER1);
    String response = client.target(API_URI).path("ping").request(MediaType.TEXT_PLAIN)
        .get(String.class);
    assertEquals(AbstractPlayerGatewayService.PING_RESULT, response);
  }

  @Test
  public void testGetCurrentPlayer() {
    Client client = createConnection(TEST_PLAYER1);
    MongoManualPlayer p = client.target(PLAYER_API).request(MediaType.APPLICATION_JSON)
        .get(MongoManualPlayer.class);
    assertEquals(TEST_PLAYER1.getId(), p.getId());
    assertEquals(TEST_PLAYER1.isDisabled(), p.isDisabled());
    assertEquals(TEST_PLAYER1.getDisplayName(), p.getDisplayName());
    assertEquals(TEST_PLAYER1.getMd5(), p.getMd5());
  }

  @Test
  public void testGetFriendsV2() {
    Client client = createConnection(TEST_PLAYER1);
    WebTarget path = client.target(PLAYER_API).path("friendsV2");
    Map<String, Object> friends = path.request(MediaType.APPLICATION_JSON)
        .get(new GenericType<Map<String, Object>>() {
        });
    List<Map<String, String>> players = (List<Map<String, String>>) friends
        .get(SourceBasedFriendFinder.MASKED_FRIENDS_KEY);

    assertTrue(players.stream().anyMatch(
        x -> TEST_PLAYER2.getMd5().equals(x.get("md5")) && TEST_PLAYER2.getDisplayName()
            .equals(x.get("displayName"))));
    assertTrue(players.stream().anyMatch(
        x -> TEST_PLAYER3.getMd5().equals(x.get("md5")) && TEST_PLAYER3.getDisplayName()
            .equals(x.get("displayName"))));
  }

  @Test
  public void testGetPhases() {
    Client client = createConnection(TEST_PLAYER3);
    Map<String, List<String>> features = client.target(API_URI).path("phases")
        .request(
            MediaType.APPLICATION_JSON_TYPE).get(new GenericType<Map<String, List<String>>>() {
        });
    LinkedHashMap<String, List<String>> expected = new LinkedHashMap<>(7);
    expected.put("Challenged",
        Arrays.asList(GamePhase.Challenged.getDescription(), GamePhase.Challenged.getGroupLabel()));
    expected.put("Declined",
        Arrays.asList(GamePhase.Declined.getDescription(), GamePhase.Declined.getGroupLabel()));
    expected.put("Quit",
        Arrays.asList(GamePhase.Quit.getDescription(), GamePhase.Quit.getGroupLabel()));
    expected.put("Setup",
        Arrays.asList(GamePhase.Setup.getDescription(), GamePhase.Setup.getGroupLabel()));
    expected.put("RoundOver",
        Arrays.asList(GamePhase.RoundOver.getDescription(), GamePhase.RoundOver.getGroupLabel()));
    expected.put("NextRoundStarted",
        Arrays.asList(GamePhase.NextRoundStarted.getDescription(),
            GamePhase.NextRoundStarted.getGroupLabel()));
    expected.put("Playing",
        Arrays.asList(GamePhase.Playing.getDescription(), GamePhase.Playing.getGroupLabel()));
    assertEquals(expected, features);
  }

  @Test
  public void testGetMultiplayerGames() {
    if (MultiPlayerGame.class.isAssignableFrom(internalGameClass())) {
      final MultiPlayerGame g1 = (MultiPlayerGame) newGame();
      final MultiPlayerGame g2 = (MultiPlayerGame) newGame();
      final MultiPlayerGame g3 = (MultiPlayerGame) newGame();
      final MultiPlayerGame g4 = (MultiPlayerGame) newGame();

      g1.setPlayers(Arrays.asList(TEST_PLAYER1, TEST_PLAYER2));
      g2.getPlayers().addAll(Arrays.asList(TEST_PLAYER1));
      g3.getPlayers()
          .addAll(Arrays.asList(TEST_PLAYER1, TEST_PLAYER3));
      g4.getPlayers()
          .addAll(Arrays.asList(TEST_PLAYER3, TEST_PLAYER2));
      g1.setGamePhase(GamePhase.Challenged);
      g2.setGamePhase(GamePhase.Declined);
      g3.setGamePhase(GamePhase.NextRoundStarted);
      g4.setGamePhase(GamePhase.Challenged);
      g1.setInitiatingPlayer(TEST_PLAYER1.getId());
      g2.setInitiatingPlayer(TEST_PLAYER1.getId());
      g3.setInitiatingPlayer(TEST_PLAYER1.getId());
      g4.setInitiatingPlayer(TEST_PLAYER1.getId());

      gameRepository()
          .saveAll(Arrays.asList(g1, g2, g3, g4));

      GenericType<List<MaskedMultiPlayerGame>> type = new GenericType<List<MaskedMultiPlayerGame>>() {
      };
      WebTarget client = createPlayerAPITarget(TEST_PLAYER1).path("games");
      List<MaskedMultiPlayerGame> foundGames = client.request(MediaType.APPLICATION_JSON).get(type);

      //  Other tests can make this result set ambiguous
      assertTrue(3 <= foundGames.size());
      assertTrue(foundGames.stream().anyMatch(x -> g1.getIdAsString().equals(x.getIdAsString())));
      assertTrue(foundGames.stream().anyMatch(x -> g2.getIdAsString().equals(x.getIdAsString())));
      assertTrue(foundGames.stream().anyMatch(x -> g3.getIdAsString().equals(x.getIdAsString())));
      assertTrue(foundGames.stream().noneMatch(x -> g4.getIdAsString().equals(x.getIdAsString())));
    }

  }

  protected G getGame(WebTarget target) {
    return (G) target.request(MediaType.APPLICATION_JSON).get(returnedGameClass());
  }

  protected G acceptGame(WebTarget target) {
    return (G) target.path("accept").request(MediaType.APPLICATION_JSON)
        .put(EMPTY_PUT_POST, returnedGameClass());
  }

  protected G rejectGame(WebTarget target) {
    return (G) target.path("reject").request(MediaType.APPLICATION_JSON)
        .put(EMPTY_PUT_POST, returnedGameClass());
  }

  protected G quitGame(WebTarget target) {
    return (G) target.path("quit").request(MediaType.APPLICATION_JSON)
        .put(EMPTY_PUT_POST, returnedGameClass());
  }

  protected G rematchGame(WebTarget target) {
    return (G) target.path("rematch").request(MediaType.APPLICATION_JSON)
        .put(EMPTY_PUT_POST, returnedGameClass());
  }
}
