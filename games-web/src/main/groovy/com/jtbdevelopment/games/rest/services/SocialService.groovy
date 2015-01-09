package com.jtbdevelopment.games.rest.services

import com.jtbdevelopment.games.security.spring.social.facebook.FacebookProperties
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 12/25/14
 * Time: 9:10 PM
 */
@Path("social")
@Component
@CompileStatic
class SocialService {
    @Autowired(required = false)
    FacebookProperties facebookProperties

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("apis")
    Map<String, String> apiInfo() {
        Map<String, String> apis = [:]
        if (facebookProperties && !facebookProperties.warnings) {
            apis['facebookAppId'] = facebookProperties.clientID
        }
        apis
    }
}
