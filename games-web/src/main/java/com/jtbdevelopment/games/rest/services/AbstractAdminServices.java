package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.AbstractPlayer;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
import com.jtbdevelopment.games.state.AbstractGame;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Date: 11/27/2014 Time: 6:34 PM
 *
 * Abstract to allow additional changes
 */
@RolesAllowed({PlayerRoles.ADMIN})
public abstract class AbstractAdminServices<
    ID extends Serializable,
    FEATURES,
    IMPL extends AbstractGame<ID, FEATURES>,
    P extends AbstractPlayer<ID>> {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 500;
  private final AbstractPlayerRepository<ID, P> playerRepository;
  private final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository;
  private final StringToIDConverter<ID> stringToIDConverter;

  @SuppressWarnings("WeakerAccess")
  protected AbstractAdminServices(
      final AbstractPlayerRepository<ID, P> playerRepository,
      final AbstractGameRepository<ID, FEATURES, IMPL> gameRepository,
      final StringToIDConverter<ID> stringToIDConverter) {
    this.playerRepository = playerRepository;
    this.gameRepository = gameRepository;
    this.stringToIDConverter = stringToIDConverter;
  }

  @GET
  @Path("gamesSince/{since}")
  @Produces(MediaType.TEXT_PLAIN)
  public long gamesSince(@PathParam("since") long since) {
    return gameRepository.countByCreatedGreaterThan(Instant.ofEpochSecond(since));
  }

  @GET
  @Path("playerCount")
  @Produces(MediaType.TEXT_PLAIN)
  public long players() {
    return playerRepository.count();
  }

  @GET
  @Path("gameCount")
  @Produces(MediaType.TEXT_PLAIN)
  public long games() {
    return gameRepository.count();
  }

  @GET
  @Path("playersCreated/{since}")
  @Produces(MediaType.TEXT_PLAIN)
  public long playersCreatedSince(@PathParam("since") long since) {
    return playerRepository.countByCreatedGreaterThan(Instant.ofEpochSecond(since));
  }

  @GET
  @Path("playersLoggedIn/{since}")
  @Produces(MediaType.TEXT_PLAIN)
  public long playersLoggedInSince(@PathParam("since") long since) {
    return playerRepository.countByLastLoginGreaterThan(Instant.ofEpochSecond(since));
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("playersLike")
  public Object playersToSimulateLike(@QueryParam("like") String like,
      @QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize) {
    return playerRepository.findByDisplayNameContains(like,
        PageRequest.of(
            page != null ? page : DEFAULT_PAGE,
            pageSize != null ? pageSize : DEFAULT_PAGE_SIZE,
            Direction.ASC, "displayName"));
  }

  @PUT
  @Path("{playerID}")
  @Produces(MediaType.APPLICATION_JSON)
  public Object switchEffectiveUser(@PathParam("playerID") final String effectivePlayerID) {
    //noinspection ConstantConditions
    Optional<P> optional = playerRepository
        .findById(stringToIDConverter.convert(effectivePlayerID));
    if (optional.isPresent()) {
      P player = optional.get();
      //noinspection unchecked
      SessionUserInfo<ID, P> principal = (SessionUserInfo<ID, P>) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      principal
          .setEffectiveUser(player);
      return player;
    }

    return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN_TYPE).build();
  }
}
