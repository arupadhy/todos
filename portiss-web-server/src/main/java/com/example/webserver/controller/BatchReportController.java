package com.example.webserver.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.batchreport.BatchReportFilter;
import com.imsi.iss.portiss.rmi.api.dto.batchreport.BatchReportRow;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.storedproc.service.BatchReportLocalService;

@RestController
public class BatchReportController {

    private final BatchReportLocalService batchReportLocalService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;

    @Autowired
    public BatchReportController(BatchReportLocalService batchReportLocalService, DataFormatter dataFormatter, FundService fundService) {
        this.batchReportLocalService = batchReportLocalService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
    }

    @RequestMapping(value = "/raw/batch-report", method = RequestMethod.POST)
    public PortissUnFormattedReport<BatchReportRow> getUnformattedBatchReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("BATCH_ID_FOR_REPORT") String batchId,
            @RequestParam("REPORT_ID") int reportId,
            @RequestParam("DATE_FROM") Date fromDate,
            @RequestParam("DATE_TO") Date toDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("BATCH_REPORT_FILTER") String batchReportFilter) {
        List<BatchReportRow> batchReportRows = batchReportLocalService.getBatchReportRows(new EntityKey<>(Fund.class, fundId), batchId, reportId, fromDate, toDate, fromHid, toHid, entityType,
                BatchReportFilter.valueOf(batchReportFilter));
        return new PortissUnFormattedReport<BatchReportRow>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, null, null, null),
                batchReportRows, Collections.<InvestorSummary> emptyList());
    }

    @PojoUsed(BatchReportRow.class)
    @RequestMapping(value = "/batch-report", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedBatchReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("BATCH_ID_FOR_REPORT") String batchId,
            @RequestParam("REPORT_ID") int reportId,
            @RequestParam("DATE_FROM") Date fromDate,
            @RequestParam("DATE_TO") Date toDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("BATCH_REPORT_FILTER") String batchReportFilter) {
        PortissUnFormattedReport<BatchReportRow> reportData = getUnformattedBatchReport(fundId, batchId, reportId, fromDate, toDate,
                fromHid, toHid, entityType, batchReportFilter);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("batchId", batchId);
        parameters.put("entityType", entityType);
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

}
