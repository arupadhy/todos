package com.example.webserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.meta.PortissArtifactsRetriever;

@RestController
public class PortissBuildDetailsController {

    @Autowired
    private PortissArtifactsRetriever artifactsRetriever;

    @RequestMapping(value = "/build/details", produces = "application/json")
    public Object retrieveManifestInfo() {
        return artifactsRetriever.retrieveDetails();
    }
}
