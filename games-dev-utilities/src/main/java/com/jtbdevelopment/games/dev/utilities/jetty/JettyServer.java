package com.jtbdevelopment.games.dev.utilities.jetty;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.atmosphere.cpr.AtmosphereServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

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
    webAppContext.setInitParameter("contextConfigLocation", "<NONE>");
    webAppContext.setResourceBase(".");
    webAppContext.setContextPath("/");
    webAppContext.setParentLoaderPriority(false);
    AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
    root.setConfigLocation("com.jtbdevelopment");
    root.register(AppConfig.class);
    webAppContext.addEventListener(new ContextLoaderListener(root));
    webAppContext.addEventListener(new RequestContextListener());
    webAppContext.setLogger(new Slf4jLog());

    configureAtmosphere(webAppContext);

    configureJersey(webAppContext);

    webAppContext
        .addFilter(new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain")), "/*",
            EnumSet.allOf(DispatcherType.class));

    server.setHandler(webAppContext);

    // Add Spring Security Filter by the name
    return server;
  }

  private static void configureJersey(WebAppContext webAppContext) {
    ServletHolder jerseyServlet = webAppContext.addServlet(ServletContainer.class, "/api/*");
    jerseyServlet.setInitOrder(2);
    jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.jtbdevelopment");
    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
        "org.glassfish.jersey.filter.LoggingFilter");
    jerseyServlet.setInitParameter("jersey.config.server.tracing", "ALL");
  }

  private static void configureAtmosphere(WebAppContext webAppContext) {
    ServletHolder atmosphereServletHolder = webAppContext
        .addServlet(AtmosphereServlet.class, "/livefeed/*");
    atmosphereServletHolder.setInitOrder(1);
    atmosphereServletHolder.setInitParameter("org.atmosphere.cpr.packages", "com.jtbdevelopment");
    atmosphereServletHolder
        .setInitParameter("org.atmosphere.websocket.messageContentType", "application/json");
    atmosphereServletHolder
        .setInitParameter("org.atmosphere.cpr.broadcasterLifeCyclePolicy", "EMPTY_DESTROY");
    atmosphereServletHolder.setInitParameter("org.atmosphere.cpr.sessionSupport", "true");
    atmosphereServletHolder.setInitParameter(
        "org.atmosphere.interceptor.HeartbeatInterceptor.heartbeatFrequencyInSeconds", "20");
    atmosphereServletHolder
        .setInitParameter("org.atmosphere.cpr.dropAccessControlAllowOriginHeader", "true");
    atmosphereServletHolder.setAsyncSupported(true);
  }

  public static void main(final String[] args) throws Exception {
    Server server = makeServer(8080);
    server.start();
    Thread.sleep(Long.MAX_VALUE);
    server.stop();
  }

}
