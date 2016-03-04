package com.example.webserver.advice;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.webserver.config.PortissWebApplicationInitializer;
import com.imsi.iss.portiss.server.service.local.EnvironmentService;

@Component
public class EnvironmentChecker {

    private final String environmentFromPropertiesFile;
    private final EnvironmentService environmentService;

    @Autowired
    public EnvironmentChecker(@Value("${" + PortissWebApplicationInitializer.ENVIRONMENT_KEY + "}") String environmentFromPropertiesFile, EnvironmentService environmentService) {
        this.environmentFromPropertiesFile = environmentFromPropertiesFile;
        this.environmentService = environmentService;
    }

    @PostConstruct
    public void checkForCorrectEnvironment() {
        if (!environmentService.isCorrectEnvironemnt(environmentFromPropertiesFile)) {
            throw new RuntimeException("Portiss Server is not running in the correct enviornment");
        }
    }

}
