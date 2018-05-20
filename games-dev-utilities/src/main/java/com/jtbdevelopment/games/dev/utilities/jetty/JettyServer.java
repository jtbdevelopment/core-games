package com.jtbdevelopment.games.dev.utilities.jetty;

import com.jtbdevelopment.core.hazelcast.sessions.SessionInitializer;
import com.jtbdevelopment.games.security.spring.security.SecurityInitializer;
import com.jtbdevelopment.games.webapp.CoreWebConfig;
import com.jtbdevelopment.games.websocket.AtmosphereWebConfig;
import java.util.HashSet;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.WebApplicationInitializer;

/**
 * Date: 12/20/2014
 * Time: 3:24 PM
 *
 * Run with VM params like
 * -Dfacebook.clientID=1234 -Dfacebook.clientSecret=5678 -Dmongo.dbName=adb -Dmongo.userName=auser -Dmongo.userPassword=apassword
 */
public class JettyServer {

  public static Server makeServer(int port) throws Exception {
    Server server = new Server(port);

    WebAppContext webAppContext = new WebAppContext();
    webAppContext.setResourceBase(".");
    webAppContext.setContextPath("/");
    webAppContext.setParentLoaderPriority(true);
    webAppContext.setConfigurations(
        new Configuration[]{new AnnotationConfiguration() {
          public void preConfigure(WebAppContext context) {
            final ClassInheritanceMap map = new ClassInheritanceMap();
            final HashSet<String> set = new HashSet<>();
            set.add(CoreWebConfig.class.getName());
            set.add(AtmosphereWebConfig.class.getName());
            set.add(SecurityInitializer.class.getName());
            set.add(SessionInitializer.class.getName());
            map.put(WebApplicationInitializer.class.getName(), set);
            context.setAttribute(CLASS_INHERITANCE_MAP, map);
            _classInheritanceHandler = new ClassInheritanceHandler(map);
          }
        }});
    webAppContext.setLogger(new Slf4jLog());

    server.setHandler(webAppContext);

    return server;
  }

  public static void main(final String[] args) throws Exception {
    Server server = makeServer(8080);
    server.start();
    Thread.sleep(Long.MAX_VALUE);
    server.stop();
  }

}
