package com.jtbdevelopment.games.websocket

import groovy.transform.CompileStatic
import org.atmosphere.cpr.AtmosphereServlet
import org.springframework.core.annotation.Order
import org.springframework.web.WebApplicationInitializer

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletRegistration

/**
 * Date: 10/18/16
 * Time: 6:45 PM
 */
@Order(value = -50)
@CompileStatic
class AtmosphereWebConfig implements WebApplicationInitializer {

    void onStartup(final ServletContext servletContext) throws ServletException {

        ServletRegistration.Dynamic atmosphere = servletContext.addServlet("ATMOSPHERE", AtmosphereServlet.class)
        atmosphere.loadOnStartup = 1
        atmosphere.setInitParameters([
                "org.atmosphere.cpr.packages"                                                : "com.jtbdevelopment",
                "org.atmosphere.websocket.messageContentType"                                : "application/json",
                "org.atmosphere.cpr.broadcasterLifeCyclePolicy"                              : "EMPTY_DESTROY",
                "org.atmosphere.cpr.sessionSupport"                                          : "true",
                "org.atmosphere.interceptor.HeartbeatInterceptor.heartbeatFrequencyInSeconds": "20",
                "org.atmosphere.cpr.dropAccessControlAllowOriginHeader"                      : "true"
        ]);
        atmosphere.asyncSupported = true
        atmosphere.addMapping("/livefeed/*")

    }
}
