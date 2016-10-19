package com.jtbdevelopment.games.dev.utilities.jetty

import groovy.transform.CompileStatic
import org.atmosphere.cpr.AtmosphereServlet
import org.eclipse.jetty.http.HttpVersion
import org.eclipse.jetty.server.*
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.log.Slf4jLog
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.webapp.WebAppContext
import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.request.RequestContextListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy

import javax.servlet.DispatcherType
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Date: 12/20/2014
 * Time: 3:24 PM
 *
 * Run with VM params like
 * -Dfacebook.clientID=1234 -Dfacebook.clientSecret=5678 -Dmongo.dbName=adb -Dmongo.userName=auser -Dmongo.userPassword=apassword
 */
@CompileStatic
class JettyServer {
    static Server makeServer(int port) {
        Server server = new Server(port)

        configureHttps(port, server)

        WebAppContext webAppContext = new WebAppContext()
        webAppContext.setInitParameter("contextConfigLocation", "<NONE>");
        webAppContext.setResourceBase(".")
        webAppContext.setContextPath("/")
        webAppContext.setParentLoaderPriority(false)
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext()
        root.configLocation = "com.jtbdevelopment"
        root.register(AppConfig.class)
        webAppContext.addEventListener(new ContextLoaderListener(root))
        webAppContext.addEventListener(new RequestContextListener())
        webAppContext.logger = new Slf4jLog()

        configureAtmosphere(webAppContext)

        configureJersey(webAppContext)

        webAppContext.addFilter(new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain")), "/*", EnumSet.allOf(DispatcherType.class))

        server.setHandler(webAppContext)

        // Add Spring Security Filter by the name
        server
    }

    private static void configureJersey(WebAppContext webAppContext) {
        ServletHolder jerseyServlet = webAppContext.addServlet(ServletContainer.class, "/api/*")
        jerseyServlet.setInitOrder(2);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.jtbdevelopment");
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", "org.glassfish.jersey.filter.LoggingFilter");
        jerseyServlet.setInitParameter("jersey.config.server.tracing", "ALL");
    }

    private static void configureAtmosphere(WebAppContext webAppContext) {
        ServletHolder atmosphereServletHolder = webAppContext.addServlet(AtmosphereServlet.class, "/livefeed/*")
        atmosphereServletHolder.setInitOrder(1)
        atmosphereServletHolder.setInitParameter("org.atmosphere.cpr.packages", "com.jtbdevelopment")
        atmosphereServletHolder.setInitParameter("org.atmosphere.websocket.messageContentType", "application/json")
        atmosphereServletHolder.setInitParameter("org.atmosphere.cpr.broadcasterLifeCyclePolicy", "EMPTY_DESTROY")
        atmosphereServletHolder.setInitParameter("org.atmosphere.cpr.sessionSupport", "true")
        atmosphereServletHolder.setInitParameter("org.atmosphere.interceptor.HeartbeatInterceptor.heartbeatFrequencyInSeconds", "20")
        atmosphereServletHolder.setInitParameter("org.atmosphere.cpr.dropAccessControlAllowOriginHeader", "true")
        atmosphereServletHolder.asyncSupported = true
    }

    //  NOTE - this keystore and password is just from jetty example pack and is not secure
    private static void configureHttps(int port, Server server) {
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(port + 1);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.trustAll = true

        def resource = JettyServer.classLoader.getResourceAsStream("keystore")
        Path path = Files.createTempFile('keystore', 'tmp')
        Files.copy(resource, path, StandardCopyOption.REPLACE_EXISTING)
        def fullPath = path.toAbsolutePath().toString()
        sslContextFactory.setKeyStorePath(fullPath)
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        sslContextFactory.setTrustStorePath(fullPath)
        sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        // SSL HTTP Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        sslConnector.setPort(port + 1);
        server.addConnector(sslConnector);
    }

    static void main(final String[] args) throws Exception {
        Server server = makeServer(9998);
        server.start()
        Thread.sleep(Long.MAX_VALUE);
        server.stop()
    }
}
