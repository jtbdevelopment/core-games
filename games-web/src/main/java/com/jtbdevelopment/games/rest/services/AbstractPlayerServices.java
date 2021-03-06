package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.exceptions.input.PlayerNotPartOfGameException;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.players.friendfinder.FriendFinder;
import com.jtbdevelopment.games.state.AbstractGame;
import com.jtbdevelopment.games.state.masking.AbstractMaskedGame;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * Date: 11/14/14 Time: 6:40 AM
 */
public abstract class AbstractPlayerServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    M extends AbstractMaskedGame<FEATURES>,
    P extends AbstractPlayer<ID>>
    implements ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(AbstractPlayerServices.class);
  private final AbstractGameServices<ID, FEATURES, IMPL, M, P> gamePlayServices;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final AbstractAdminServices<ID, FEATURES, IMPL, P> adminServices;
  private final StringToIDConverter<ID> stringToIDConverter;
  private ThreadLocal<ID> playerID = new ThreadLocal<>();
  private ApplicationContext applicationContext;

  @SuppressWarnings("WeakerAccess")
  protected AbstractPlayerServices(
      final AbstractGameServices<ID, FEATURES, IMPL, M, P> gamePlayServices,
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractAdminServices<ID, FEATURES, IMPL, P> adminServices,
      final StringToIDConverter<ID> stringToIDConverter) {
    this.gamePlayServices = gamePlayServices;
    this.playerRepository = playerRepository;
    this.adminServices = adminServices;
    this.stringToIDConverter = stringToIDConverter;
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Path("game/{gameID}")
  public Object gamePlay(@PathParam("gameID") final String gameID) {
    if (StringUtils.isEmpty(gameID) || StringUtils.isEmpty(gameID.trim())) {
      return Response.status(Status.BAD_REQUEST).entity("Missing game identity").build();
    }

    gamePlayServices.getGameID().set(stringToIDConverter.convert(gameID));
    gamePlayServices.getPlayerID().set(playerID.get());
    return gamePlayServices;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Object playerInfo() {
    return playerRepository.findById(playerID.get()).orElseGet(null);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("friendsV2")
  public Map<String, Set<? super Object>> getFriendsV2() {
    //  Social Media Requires Session Specific Requests
    if (applicationContext != null) {
      logger.info("Able to retrieve FriendFinder from application context");
      //noinspection unchecked
      FriendFinder<ID, P> friendFinder = applicationContext.getBean(FriendFinder.class);
      return friendFinder.findFriendsV2(playerID.get());
    } else {
      logger.warn("Unable to retrieve FriendFinder from application context");
      throw new IllegalStateException("No App Context");
    }

  }

  @Path("lastVersionNotes/{versionNotes}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Object updateLastVersionNotes(@PathParam("versionNotes") final String lastVersionNotes) {
    Optional<P> byId = playerRepository.findById((playerID.get()));
    if (byId.isPresent()) {
      P player = byId.get();
      player.setLastVersionNotes(lastVersionNotes);
      return playerRepository.save(player);
    }
    throw new PlayerNotPartOfGameException();
  }

  @Path("admin")
  @RolesAllowed({PlayerRoles.ADMIN})
  public Object adminServices() {
    return adminServices;
  }

  public ThreadLocal<ID> getPlayerID() {
    return playerID;
  }

  public void setPlayerID(ThreadLocal<ID> playerID) {
    this.playerID = playerID;
  }
}
