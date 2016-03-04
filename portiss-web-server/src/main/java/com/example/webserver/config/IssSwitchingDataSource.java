package com.example.webserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.imsi.iss.portiss.server.entity.IssDatabaseKey;
import com.imsi.iss.portiss.server.service.local.IssDataSourceSwitchingService;

public class IssSwitchingDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssSwitchingDataSource.class);

    private final IssDataSourceSwitchingService issDataSourceSwitchingService;

    public IssSwitchingDataSource(IssDataSourceSwitchingService issDataSourceSwitchingService) {
        this.issDataSourceSwitchingService = issDataSourceSwitchingService;
    }

    @Override
    protected IssDatabaseKey determineCurrentLookupKey() {
        IssDatabaseKey issDatabaseKey = issDataSourceSwitchingService.getDatabaseToConnect();
        LOGGER.trace("Connecting to the DB with key {}", issDatabaseKey);
        return issDatabaseKey;
    }

}
