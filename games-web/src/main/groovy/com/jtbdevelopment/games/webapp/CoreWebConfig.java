package com.jtbdevelopment.games.webapp;

import java.util.LinkedHashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Date: 10/18/16 Time: 6:45 PM
 */
@Order(value = -100)
public class CoreWebConfig implements WebApplicationInitializer {

    public void onStartup(final ServletContext servletContext) throws ServletException {
        servletContext.setInitParameter("contextConfigLocation", "<NONE>");
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(SpringWebConfig.class);
        root.setConfigLocation("com.jtbdevelopment");
        servletContext.addListener(new ContextLoaderListener(root));

        Dynamic jersey = servletContext.addServlet("REST", ServletContainer.class);
        jersey.setLoadOnStartup(0);
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(3);
        map.put("jersey.config.server.provider.packages", "com.jtbdevelopment");
        map.put("jersey.config.server.provider.classnames",
            "org.glassfish.jersey.filter.LoggingFilter;org.glassfish.jersey.message.DeflateEncoder;org.glassfish.jersey.message.GZipEncoder;org.glassfish.jersey.server.filter.EncodingFilter");
        map.put("jersey.config.server.tracing", "ALL");
        jersey.setInitParameters(map);
        jersey.addMapping("/api/*");
    }

}
