package com.example.webserver.controller.partnerstatement;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementLpStrategicValueReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementStrategicValue;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementStrategicValueReport;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.EntityDto;
import com.imsi.iss.portiss.rmi.dto.FundDto;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.local.ReportService;
import com.imsi.iss.portiss.storedproc.service.PartnerStatementStrategicValueLocalService;

@RestController
public class StrategicValuePartnerStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrategicValuePartnerStatementController.class);

    private final PartnerStatementStrategicValueLocalService partnerStatementStrategicValueLocalService;
    private final FundService fundService;
    private final DataFormatter dataFormatter;
    private final CsvExporter csvExporter;
    private final ReportService reportService;

    @Autowired
    public StrategicValuePartnerStatementController(PartnerStatementStrategicValueLocalService partnerStatementStrategicValueLocalService, FundService fundService, DataFormatter dataFormatter,
            @Qualifier("partnerStatementCsvExporter") CsvExporter csvExporter, ReportService reportService) {
        this.partnerStatementStrategicValueLocalService = partnerStatementStrategicValueLocalService;
        this.fundService = fundService;
        this.dataFormatter = dataFormatter;
        this.csvExporter = csvExporter;
        this.reportService = reportService;
    }

    @RequestMapping(value = "/raw/partner-statement/strategic-value", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementStrategicValueReport> getUnFormattedPartnerStatementStrategicValue(@RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam(value = "FUND_SUMMARY", required = false) int fundSummary,
            @RequestParam(value = "SUMMARY_FUNDS", required = false) String summaryFunds,
            @RequestParam(value = "FUND_TO_DATE_INFO", required = false) int fundToDateInfo,
            @RequestParam(value = "SORT_ORDER_PS", required = false) int sortOrder,
            @RequestParam(value = "SIGNATURE_INFO", required = false) int signatureInfo,
            @RequestParam(value = "FIRST_NAME_FIRST", required = false) int firstNameFirst,
            @RequestParam(value = "COMBINE_RESULTS", required = false) int combineResults,
            @RequestParam(value = "LOAD_RSSPLIT", required = false) int loadRssplit,
            @RequestParam(value = "SPECIAL_LOGIC", required = false) String specialLogic,
            @RequestParam(value = "SHOW_CLASS_DATA", required = false) int showClassData) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        PartnerStatementStrategicValueReport partnerStatementStrategicValueReport = partnerStatementStrategicValueLocalService.getPartnerStatementStrategicValues(fundKey, yearDate, previousDate,
                statementDate,
                fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo,
                firstNameFirst, combineResults, loadRssplit, specialLogic, showClassData);

        List<PartnerStatementStrategicValue> partnerStatementStrategicValues = partnerStatementStrategicValueReport.getPartnerStatementStrategicValues();
        Optional<PartnerStatementStrategicValue> optionalPartnerStatementStrategicValue = Optional.of(partnerStatementStrategicValues.get(0));

        List<InvestorSummary> investorSummaryData = partnerStatementStrategicValueReport.getInvestorSummaryData();

        PortissUnFormattedReport<PartnerStatementStrategicValueReport> unFormattedReport = new PortissUnFormattedReport<PartnerStatementStrategicValueReport>(
                getReportHeader(fundDto, optionalPartnerStatementStrategicValue), Arrays.asList(partnerStatementStrategicValueReport), investorSummaryData);
        return unFormattedReport;

    }

    @PojoUsed(PartnerStatementStrategicValueReport.class)
    @RequestMapping(value = "/partner-statement/strategic-value", method = RequestMethod.POST)
    public PortissFormattedReport getPartnerStatementStrategicValueReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam(value = "FUND_SUMMARY", required = false) int fundSummary,
            @RequestParam(value = "SUMMARY_FUNDS", required = false) String summaryFunds,
            @RequestParam(value = "FUND_TO_DATE_INFO", required = false) int fundToDateInfo,
            @RequestParam(value = "SORT_ORDER_PS", required = false) int sortOrder,
            @RequestParam(value = "SIGNATURE_INFO", required = false) int signatureInfo,
            @RequestParam(value = "FIRST_NAME_FIRST", required = false) int firstNameFirst,
            @RequestParam(value = "COMBINE_RESULTS", required = false) int combineResults,
            @RequestParam(value = "LOAD_RSSPLIT", required = false) int loadRssplit,
            @RequestParam(value = "SPECIAL_LOGIC", required = false) String specialLogic,
            @RequestParam(value = "SHOW_CLASS_DATA", required = false) int showClassData,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        PortissUnFormattedReport<?> reportData = getUnFormattedPartnerStatementStrategicValue(fundId, yearDate, previousDate, statementDate,
                fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo,
                firstNameFirst, combineResults, loadRssplit, specialLogic, showClassData);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("csvExportPath", csvExportPath);
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);
    }
    
    @RequestMapping(value = "/raw/partner-statement/lp-strategic-value", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementLpStrategicValueReport> getUnFormattedPartnerStatementLpStrategicValue(
    		@RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam(value = "FUND_SUMMARY", required = false) int fundSummary,
            @RequestParam(value = "SUMMARY_FUNDS", required = false) String summaryFunds,
            @RequestParam(value = "FUND_TO_DATE_INFO", required = false) int fundToDateInfo,
            @RequestParam(value = "SORT_ORDER_PS", required = false) int sortOrder,
            @RequestParam(value = "SIGNATURE_INFO", required = false) int signatureInfo,
            @RequestParam(value = "FIRST_NAME_FIRST", required = false) int firstNameFirst,
            @RequestParam(value = "COMBINE_RESULTS", required = false) int combineResults,
            @RequestParam(value = "LOAD_RSSPLIT", required = false) int loadRssplit,
            @RequestParam(value = "SPECIAL_LOGIC", required = false) String specialLogic,
            @RequestParam(value = "SHOW_CLASS_DATA", required = false) int showClassData) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        PartnerStatementLpStrategicValueReport partnerStatementStrategicValueReport = partnerStatementStrategicValueLocalService.getPartnerStatementLpStrategicValues(fundKey, yearDate, previousDate,
                statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, firstNameFirst, combineResults, loadRssplit, specialLogic, showClassData);

        List<PartnerStatementStrategicValue> partnerStatementStrategicValues = partnerStatementStrategicValueReport.getPartnerStatementStrategicValues();
        Optional<PartnerStatementStrategicValue> optionalPartnerStatementStrategicValue = Optional.of(partnerStatementStrategicValues.get(0));

        List<InvestorSummary> investorSummaryData = partnerStatementStrategicValueReport.getInvestorSummaryData();

        PortissUnFormattedReport<PartnerStatementLpStrategicValueReport> unFormattedReport = new PortissUnFormattedReport<PartnerStatementLpStrategicValueReport>(
                getReportHeader(fundDto, optionalPartnerStatementStrategicValue), Arrays.asList(partnerStatementStrategicValueReport), investorSummaryData);
        return unFormattedReport;

    }

    @PojoUsed(PartnerStatementLpStrategicValueReport.class)
    @RequestMapping(value = "/partner-statement/lp-strategic-value", method = RequestMethod.POST)
    public PortissFormattedReport getPartnerStatementLpStrategicValueReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam(value = "FUND_SUMMARY", required = false) int fundSummary,
            @RequestParam(value = "SUMMARY_FUNDS", required = false) String summaryFunds,
            @RequestParam(value = "FUND_TO_DATE_INFO", required = false) int fundToDateInfo,
            @RequestParam(value = "SORT_ORDER_PS", required = false) int sortOrder,
            @RequestParam(value = "SIGNATURE_INFO", required = false) int signatureInfo,
            @RequestParam(value = "FIRST_NAME_FIRST", required = false) int firstNameFirst,
            @RequestParam(value = "COMBINE_RESULTS", required = false) int combineResults,
            @RequestParam(value = "LOAD_RSSPLIT", required = false) int loadRssplit,
            @RequestParam(value = "SPECIAL_LOGIC", required = false) String specialLogic,
            @RequestParam(value = "SHOW_CLASS_DATA", required = false) int showClassData,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        PortissUnFormattedReport<?> reportData = getUnFormattedPartnerStatementLpStrategicValue(fundId, yearDate, previousDate, statementDate,
                fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo,
                firstNameFirst, combineResults, loadRssplit, specialLogic, showClassData);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("csvExportPath", csvExportPath);
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);
    }

    private PortissReportHeader getReportHeader(FundDto fundDto, Optional<PartnerStatementStrategicValue> optionalPartnerStatementStrategicValue) {

        Map<String, Object> userObjects;
        if (optionalPartnerStatementStrategicValue.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("fundInfo", optionalPartnerStatementStrategicValue.get());
        } else {
            userObjects = Collections.emptyMap();
        }

        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundDto.getFundId()), "JASPER_PARTNER_STMT");
        return new PortissReportHeader(entityDto, userObjects, null, maskedFileName, null, null);
    }
}
