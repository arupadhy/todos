package com.example.webserver.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortissArtifactsRetriever {

    private Properties manifestDetails = new Properties();
    private static final Logger LOGGER = LoggerFactory.getLogger(PortissArtifactsRetriever.class);

    private void loadDetails() {
        LOGGER.debug(String.format("retrieving build details for portiss-web-server"));
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/META-INF/MANIFEST.MF");
        if (in == null) {
            //local env
            LOGGER.info(String.format("Could not find manifest in the war"));
            File manifestFile = new File(".\\target\\m2e-wtp\\web-resources\\META-INF\\MANIFEST.MF");
            if (manifestFile.exists()) {
                try {
                    in = new FileInputStream(manifestFile);
                } catch (FileNotFoundException e) {
                }
            } else {
                LOGGER.info(String.format("Could not find %s...Local portiss server running", manifestFile.getAbsolutePath()));
                //local env info..no need to return any info
                manifestDetails.put("build-id", "Running locally");
                return;
            }
        }
        try {
            manifestDetails.load(in);
        } catch (IOException e) {
            LOGGER.error(String.format("Could not populate %s in the portiss-web-war file...skipping", manifestDetails.elements()), e);
        }
    }

    public Properties retrieveDetails() {
        loadDetails();
        return manifestDetails;
    }
}
