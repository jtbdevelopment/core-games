package com.jtbdevelopment.games.websocket;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import org.atmosphere.cpr.AtmosphereServlet;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * Date: 10/18/16 Time: 6:45 PM
 */
@Order(value = -50)
public class AtmosphereWebConfig implements WebApplicationInitializer {

  public void onStartup(final ServletContext servletContext) throws ServletException {

    Dynamic atmosphere = servletContext.addServlet("ATMOSPHERE", AtmosphereServlet.class);
    atmosphere.setLoadOnStartup(1);
    Map<String, String> map = new HashMap<>(6);
    map.put("org.atmosphere.cpr.packages", "com.jtbdevelopment");
    map.put("org.atmosphere.websocket.messageContentType", "application/json");
    map.put("org.atmosphere.cpr.broadcasterLifeCyclePolicy", "EMPTY_DESTROY");
    map.put("org.atmosphere.cpr.sessionSupport", "true");
    map.put("org.atmosphere.interceptor.HeartbeatInterceptor.heartbeatFrequencyInSeconds", "20");
    map.put("org.atmosphere.cpr.dropAccessControlAllowOriginHeader", "true");
    atmosphere.setInitParameters(map);
    atmosphere.setAsyncSupported(true);
    atmosphere.addMapping("/livefeed/*");

  }

}
