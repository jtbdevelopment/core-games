package com.jtbdevelopment.games.rest.services;

import com.jtbdevelopment.games.security.spring.social.facebook.FacebookProperties;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Date: 12/25/14 Time: 9:10 PM
 */
@Path("social")
@Component
public class SocialService {

  @Autowired(required = false)
  protected FacebookProperties facebookProperties;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("apis")
  public Map<String, String> apiInfo() {
    Map<String, String> apis = new HashMap<String, String>() {{
      if (facebookProperties != null && !facebookProperties.isWarnings()) {
        put("facebookAppId", facebookProperties.getClientID());
        put("facebookPermissions", facebookProperties.getPermissions());
      }
    }};

    return apis;
  }
}
