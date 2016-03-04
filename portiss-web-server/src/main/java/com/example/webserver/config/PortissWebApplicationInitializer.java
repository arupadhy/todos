package com.example.webserver.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import net.sf.ehcache.constructs.web.filter.GzipFilter;

public class PortissWebApplicationInitializer implements WebApplicationInitializer {

    public static final String ENVIRONMENT_KEY = "environment";

    // set the location of log4j xml, as it wont be available in the default location
    static {
        System.setProperty("log4j.configurationFile", "/environments/" + System.getProperty(ENVIRONMENT_KEY) + "/log4j2.xml");
    }

    @Override
    public void onStartup(ServletContext container) {
        // Create the dispatcher servlet's Spring application context
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(PortissServiceConfig.class, PortissDaoConfig.class);

        // Register and map the dispatcher servlet
        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        FilterRegistration.Dynamic gzipFilter = container.addFilter("GzipFilter", GzipFilter.class);
        gzipFilter.addMappingForUrlPatterns(null, true, "*");
    }

}
