package com.example.webserver.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.imsi.cw.provider.PasswordProviderException;
import com.imsi.iss.portiss.server.entity.IssDatabaseKey;
import com.imsi.iss.portiss.server.service.local.IssDataSourceSwitchingService;
import com.imsi.jdbc.ImsJdbcTemplate;
import com.imsi.jdbc.ImsJdbcTemplateImpl;
import com.imsi.jdbc.ImsJdbcTemplateWithLogging;
import com.imsi.jdbc.ImsJdbcTemplateWithMetrics;
import com.imsi.metrics.sql.SqlExecutionDataSerializer;
import com.imsi.metrics.sql.SqlExecutionMetricsBroadcaster;

import Matrix.PwMatrix;

@Configuration
@EnableTransactionManagement
public class PortissDaoConfig {

    @Autowired
    private Environment environment;

    @Bean(destroyMethod = "close")
    public DataSource issReportingReadOnlyDataSource() throws PasswordProviderException {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
        basicDataSource.setUrl(
                new StringBuilder(70)
                        .append("jdbc:sybase:Tds:")
                        .append(environment.getProperty("report.db.host"))
                        .append(":")
                        .append(environment.getProperty("report.db.port"))
                        .append("?ServiceName=")
                        .append(environment.getProperty("report.db.serviceName"))
                        .toString());
        String userName = environment.getProperty("report.db.user");
        basicDataSource.setUsername(userName);
        basicDataSource.setPassword(PwMatrix.getPassword(environment.getProperty("cloakware.db.server.key"), userName));
        basicDataSource.setValidationQuery("SELECT 1");
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxWait(5000);
        basicDataSource.setInitialSize(10);
        basicDataSource.setMaxIdle(20);
        return basicDataSource;
    }

    @Bean(destroyMethod = "close")
    public DataSource issMainDataSource() throws PasswordProviderException {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
        basicDataSource.setUrl(
                new StringBuilder(70)
                        .append("jdbc:sybase:Tds:")
                        .append(environment.getProperty("issMain.db.host"))
                        .append(":")
                        .append(environment.getProperty("issMain.db.port"))
                        .append("?ServiceName=")
                        .append(environment.getProperty("issMain.db.serviceName"))
                        .toString());
        String userName = environment.getProperty("issMain.db.user");
        basicDataSource.setUsername(userName);
        basicDataSource.setPassword(PwMatrix.getPassword(environment.getProperty("cloakware.db.server.key"), userName));
        basicDataSource.setValidationQuery("SELECT 1");
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxWait(5000);
        basicDataSource.setInitialSize(10);
        basicDataSource.setMaxIdle(20);
        return basicDataSource;
    }

    @Bean
    public IssSwitchingDataSource issSwitchingDataSource(
            @Qualifier("issMainDataSource") DataSource issMainDataSource,
            @Qualifier("issReportingReadOnlyDataSource") DataSource issReportingReadOnlyDataSource,
            IssDataSourceSwitchingService issDataSourceSwitchingService) {

        IssSwitchingDataSource issSwitchingDataSource = new IssSwitchingDataSource(issDataSourceSwitchingService);

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(IssDatabaseKey.MAIN_DB, issMainDataSource);
        targetDataSources.put(IssDatabaseKey.MIRROR_DB, issReportingReadOnlyDataSource);
        issSwitchingDataSource.setTargetDataSources(targetDataSources);

        issSwitchingDataSource.setDefaultTargetDataSource(issReportingReadOnlyDataSource);

        return issSwitchingDataSource;
    }

    @Bean
    @Primary
    public ImsJdbcTemplate imsJdbcTemplate(IssSwitchingDataSource issSwitchingDataSource,
            @Qualifier("sqlExecutionMetricsBroadcaster") SqlExecutionMetricsBroadcaster sqlExecutionMetricsBroadcaster) {
        return new ImsJdbcTemplateWithLogging(new ImsJdbcTemplateWithMetrics(new ImsJdbcTemplateImpl(issSwitchingDataSource), sqlExecutionMetricsBroadcaster));
    }

    @Bean
    public ImsJdbcTemplate imsJdbcTemplateForIssMainDatabase(@Qualifier("issMainDataSource") DataSource issMainDataSource,
            @Qualifier("sqlExecutionMetricsBroadcaster") SqlExecutionMetricsBroadcaster sqlExecutionMetricsBroadcaster) {
        return new ImsJdbcTemplateWithLogging(new ImsJdbcTemplateWithMetrics(new ImsJdbcTemplateImpl(issMainDataSource), sqlExecutionMetricsBroadcaster));
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(IssSwitchingDataSource issSwitchingDataSource) {
        return new NamedParameterJdbcTemplate(issSwitchingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManagerForIssMainDataSource(@Qualifier("issMainDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public SqlExecutionMetricsBroadcaster sqlExecutionMetricsBroadcaster() {
        return new SqlExecutionMetricsBroadcaster(new SqlExecutionDataSerializer());
    }

}
