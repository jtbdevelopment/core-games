package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.dao.StringToIDConverter;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerRoles;
import com.jtbdevelopment.games.security.SessionUserInfo;
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
public abstract class AbstractAdminServices {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 500;
  private final AbstractPlayerRepository playerRepository;
  private final AbstractGameRepository gameRepository;
  private final StringToIDConverter stringToIDConverter;

  public AbstractAdminServices(
      final AbstractPlayerRepository playerRepository,
      final AbstractGameRepository gameRepository,
      final StringToIDConverter stringToIDConverter) {
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
    Optional<? extends Player> optional = playerRepository
        .findById((Serializable) stringToIDConverter.convert(effectivePlayerID));
    if (optional.isPresent()) {
      Player player = optional.get();
      ((SessionUserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
          .setEffectiveUser(player);
      return player;
    }

    return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN_TYPE).build();
  }
}
