package com.example.webserver.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.jrxml.adapter.JRGlobals;
import com.imsi.iss.portiss.jrxml.creator.builder.runners.DynamicJrxmlCreator;
import com.imsi.iss.portiss.jrxml.creator.builder.runners.DynamicJrxmlDetails;
import com.imsi.iss.portiss.rmi.dto.DataRow;
import com.imsi.iss.portiss.rmi.dto.Field;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.SimpleField;
import com.imsi.iss.portiss.server.jasper.cleanup.JrxmlCleanupService;
import com.imsi.iss.portiss.server.jasper.deploy.JasperReportDeployerService;

@RestController
public class DynamicReportTemplateController {

    private final DynamicJrxmlCreator dynamicJrxmlCreator;
    private final JasperReportDeployerService jasperReportDeployerService;
    private final JrxmlCleanupService localjrxmlCleanUpService;

    @Autowired
    public DynamicReportTemplateController(DynamicJrxmlCreator dynamicJrxmlCreator, JasperReportDeployerService jasperReportDeployerService, JrxmlCleanupService cleanUpService) {
        this.dynamicJrxmlCreator = dynamicJrxmlCreator;
        this.jasperReportDeployerService = jasperReportDeployerService;
        this.localjrxmlCleanUpService = cleanUpService;
    }

    @RequestMapping(value = "/deploy-dynamic-jrxmls", method = RequestMethod.POST)
    public PortissFormattedReport createAndDeployJrxmls(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") int reportStatementId,
            @RequestParam("FUND_ID") int fundId) {

        //FIXME: entity-type is always hard-coded to fund
        DynamicJrxmlDetails dynamicJrxmlDetails = dynamicJrxmlCreator.buildReportsAndReturnFilePath(reportRunId, fundId, 0, reportStatementId);

        jasperReportDeployerService.uploadJrxmlToJasper(dynamicJrxmlDetails);

        //clean local system 
        localjrxmlCleanUpService.cleanLocalJrxmls(dynamicJrxmlDetails.getJrxmlDirectory());

        Map<String, Field> fieldMap = new HashMap<>();
        fieldMap.put(JRGlobals.DYNAMIC_SUB_DIRECTORY_PATH, new SimpleField(dynamicJrxmlDetails.getJasperSubDirectoryPath()));
        fieldMap.put(JRGlobals.DYNAMIC_SUBREPORT_PATH, new SimpleField(dynamicJrxmlDetails.getMainJasperTemplateName()));
        List<DataRow> mainDataRows = Arrays.asList(new DataRow(fieldMap));
        return new PortissFormattedReport(null, mainDataRows, Collections.emptyList());
    }

}
