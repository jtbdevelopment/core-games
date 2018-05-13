package com.jtbdevelopment.games.dev.utilities.integrationtesting;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

/**
 * Date: 12/20/2014 Time: 4:12 PM
 *
 * Useful sanity check to verify if non-authenticated rest is working
 */
@Path("test")
@Component
public class TestService {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String get() {
    return "no auth test";
  }

}
