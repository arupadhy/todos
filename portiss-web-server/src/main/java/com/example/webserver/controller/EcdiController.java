package com.example.webserver.controller;

import java.time.ZoneId;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.ecdi.EcdiConvexityDetails;
import com.imsi.iss.portiss.rmi.api.dto.ecdi.EcdiDetail;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.EntityDto;
import com.imsi.iss.portiss.rmi.dto.FundDto;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.local.MonthEndNavService;
import com.imsi.iss.portiss.storedproc.service.EcdiLocalService;

@RestController
public class EcdiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EcdiController.class);

    private final EcdiLocalService ecdiLocalService;

    private final FundService fundService;

    private final DataFormatter dataFormatter;

    private final CsvExporter csvExporter;

    private final MonthEndNavService monthEndNavService;

    @Autowired
    public EcdiController(EcdiLocalService ecdiLocalService, FundService fundService, DataFormatter dataFormatter, CsvExporter csvExporter, MonthEndNavService monthEndNavService) {
        this.ecdiLocalService = ecdiLocalService;
        this.fundService = fundService;
        this.dataFormatter = dataFormatter;
        this.csvExporter = csvExporter;
        this.monthEndNavService = monthEndNavService;
    }

    @PojoUsed(EcdiDetail.class)
    @RequestMapping(value = "/ecdi", method = RequestMethod.POST)
    public PortissFormattedReport getEcdiDetails(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("FORMAT") int format,
            @RequestParam("RUN_MODE") int runMode,
            @RequestParam("RETURN_MODE") int returnMode,
            @RequestParam("BY_TAXLOT") int byTaxLot,
            @RequestParam(value = "SPECIAL_LOGIC", required = false) String specialLogic,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("CONVERT_TO_USD") int convertToUsd,
            @RequestParam("TLOGIC") int tlogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("format", format);
        parameters.put("runMode", runMode);
        parameters.put("returnMode", returnMode);
        parameters.put("byTaxLot", byTaxLot);
        parameters.put("specialLogic", specialLogic);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("convertToUsd", convertToUsd);
        parameters.put("tlogic", tlogic);
        parameters.put("csvExportPath", csvExportPath);

        PortissUnFormattedReport<EcdiDetail> reportData = getUnformattedEcdiDetails(fundId, statementDate, format, runMode, returnMode, byTaxLot, specialLogic, fromHid, toHid, convertToUsd, tlogic);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);

        return portissReport;
    }

    @RequestMapping(value = "/raw/ecdi", method = RequestMethod.POST)
    public PortissUnFormattedReport<EcdiDetail> getUnformattedEcdiDetails(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("FORMAT") int format,
            @RequestParam("RUN_MODE") int runMode,
            @RequestParam("RETURN_MODE") int returnMode,
            @RequestParam("BY_TAXLOT") int byTaxLot,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("CONVERT_TO_USD") int convertToUsd,
            @RequestParam("TLOGIC") int tlogic) {

        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        LOGGER.debug("WaterMark Message---" + message);
        List<EcdiDetail> mainReportData = ecdiLocalService.getEcdiDetails(fundKey, statementDate, format, runMode, returnMode, byTaxLot, specialLogic, fromHid, toHid, convertToUsd, tlogic, fundDto.getIncentiveMethod());
        PortissUnFormattedReport<EcdiDetail> unFormattedReport = new PortissUnFormattedReport<EcdiDetail>(getReportHeader(fundDto, fundKey, statementDate, message), mainReportData, Collections.<InvestorSummary> emptyList());
        return unFormattedReport;
    }

    @PojoUsed(EcdiConvexityDetails.class)
    @RequestMapping(value = "/ecdi/convexity", method = RequestMethod.POST)
    public PortissFormattedReport getEcdiConvexity(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) Date quarterDate,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("HID_FROM") int hid1,
            @RequestParam("HID_TO") int hid2,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("yearDate", yearDate);
        parameters.put("passParams", passParams);
        parameters.put("hid1", hid1);
        parameters.put("hid2", hid2);
        parameters.put("csvExportPath", csvExportPath);

        PortissUnFormattedReport<EcdiConvexityDetails> reportData = getUnformattedEcdiConvexity(fundId, statementDate, previousDate, quarterDate, yearDate, passParams, hid1, hid2);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);

        return portissReport;
    }

    @RequestMapping(value = "/raw/ecdi/convexity", method = RequestMethod.POST)
    public PortissUnFormattedReport<EcdiConvexityDetails> getUnformattedEcdiConvexity(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) Date quarterDate,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("HID_FROM") int hid1,
            @RequestParam("HID_TO") int hid2) {

        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        List<EcdiConvexityDetails> mainReportData = ecdiLocalService.getEcdiConvexity(fundKey, statementDate, previousDate, quarterDate, yearDate, passParams, hid1, hid2);
        PortissUnFormattedReport<EcdiConvexityDetails> unFormattedReport = new PortissUnFormattedReport<EcdiConvexityDetails>(getReportHeader(fundDto, fundKey, statementDate, message), mainReportData, Collections.<InvestorSummary> emptyList());
        return unFormattedReport;
    }

    //TODO: Refactor. Create New Service to create PortissReportHeader(or whatever name makes sense). Allow for reusability.
    private PortissReportHeader getReportHeader(FundDto fundDto, EntityKey<Fund> fundKey, Date statementDate, String waterMarkMessage) {
        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));

        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());

        PortissReportHeader reportHeader = new PortissReportHeader(entityDto, null, message, null, null, null);
        return reportHeader;
    }

    private String getNavMessage(EntityKey<Fund> fundKey, Date navDate, int clientId) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, navDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), clientId, 0);
        return navMessage.isPresent() ? navMessage.get() : "";
    }
}