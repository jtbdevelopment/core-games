package com.jtbdevelopment.games.webapp

import groovy.transform.CompileStatic
import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.core.annotation.Order
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletRegistration

/**
 * Date: 10/18/16
 * Time: 6:45 PM
 */
@Order(value = -100)
@CompileStatic
class CoreWebConfig implements WebApplicationInitializer {

    void onStartup(final ServletContext servletContext) throws ServletException {
        servletContext.setInitParameter("contextConfigLocation", "<NONE>")
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext()
        root.register(SpringWebConfig.class)
        root.configLocation = "com.jtbdevelopment"
        servletContext.addListener(new ContextLoaderListener(root))

        ServletRegistration.Dynamic jersey = servletContext.addServlet("REST", ServletContainer.class)
        jersey.loadOnStartup = 0
        jersey.setInitParameters([
                "jersey.config.server.provider.packages"  : "com.jtbdevelopment",
                "jersey.config.server.provider.classnames": "org.glassfish.jersey.filter.LoggingFilter;org.glassfish.jersey.message.DeflateEncoder;org.glassfish.jersey.message.GZipEncoder;org.glassfish.jersey.server.filter.EncodingFilter",
                "jersey.config.server.tracing"            : "ALL"
        ])
        jersey.addMapping("/api/*")
    }
}
