package com.example.webserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.imsi.cw.provider.PasswordProviderException;
import com.imsi.iss.portiss.client.JasperReportClient;
import com.imsi.iss.portiss.client.impl.JasperReportJrsClient;

import Matrix.PwMatrix;

@Configuration
@ComponentScan("com.imsi.iss.portiss")
@EnableAspectJAutoProxy
@PropertySource({"classpath:/environments/${" + PortissWebApplicationInitializer.ENVIRONMENT_KEY + "}/portiss.properties"})
public class PortissServiceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public JasperReportClient jasperReportClient() throws PasswordProviderException {
        String jasperServerUrl = environment.getProperty("jasperserver.deploy.url");
        String organization = environment.getProperty("jasperserver.deploy.org");
        String username = environment.getProperty("jasperserver.deploy.user");
        String cloakwareKey = environment.getProperty("jasperserver.deploy.cloakware.key");
        return new JasperReportJrsClient(jasperServerUrl, organization, username, PwMatrix.getPassword(cloakwareKey, username));
    }

    @Bean(destroyMethod = "destroy")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(25);
        return threadPoolTaskExecutor;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
