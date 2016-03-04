package com.example.webserver.controller.partnerstatement;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementGSOReport;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.EntityDto;
import com.imsi.iss.portiss.rmi.dto.FundDto;
import com.imsi.iss.portiss.rmi.dto.FundInfo;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.local.MonthEndNavService;
import com.imsi.iss.portiss.server.service.local.ReportService;
import com.imsi.iss.portiss.storedproc.service.PartnerStatementGSOLocalService;

@RestController
public class GSOPartnerStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GSOPartnerStatementController.class);
    private final PartnerStatementGSOLocalService partnerStatementGSOLocalService;
    private final MonthEndNavService monthEndNavService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;
    private final CsvExporter csvExporter;
    private final ReportService reportService;

    @Autowired
    public GSOPartnerStatementController(PartnerStatementGSOLocalService partnerStatementGSOLocalService, MonthEndNavService monthEndNavService, DataFormatter dataFormatter, FundService fundService,
            @Qualifier("partnerStatementCsvExporter") CsvExporter csvExporter, ReportService reportService) {
        this.partnerStatementGSOLocalService = partnerStatementGSOLocalService;
        this.monthEndNavService = monthEndNavService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
        this.csvExporter = csvExporter;
        this.reportService = reportService;
    }

    @PojoUsed(PartnerStatementGSOReport.class)
    @RequestMapping(value = "/partner-statement/gso", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementGSOReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("CALC_INCEP_ROR") int calcIncepROR,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam(value = "SUMMARY_FUNDS", required = false) String summaryFunds,
            @RequestParam("SORT_ORDER_PS") int sortOrder,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("csvExportPath", csvExportPath);
        parameters.put("useXref", useXref);
        parameters.put("fundSummary", fundSummary);
        parameters.put("specialLogic", specialLogic);
        parameters.put("calcIncepROR", calcIncepROR);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("sortOrderPS", sortOrder);
        parameters.put("fundToDateInfo", fundToDateInfo);

        PartnerStatementGSOReport partnerStatementGSOReportData = partnerStatementGSOLocalService.getPartnerStatementGSO(fundId, yearDate, previousDate, statementDate, fromHid, toHid, useXref,
                fundSummary, calcIncepROR, firstNameFirst, signatureInfo, summaryFunds, sortOrder, fundToDateInfo, investorLevel, specialLogic, csvType);

        List<FundInfo> partnerStatementGSOFundInfoList = partnerStatementGSOReportData.getPartnerStatementGSOFundInfo();
        Optional<FundInfo> partnerStatementGSOFundInfo = Optional.of(partnerStatementGSOFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementGSOFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementGSOFundInfo", partnerStatementGSOFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }

        List<InvestorSummary> investorSummaryData = partnerStatementGSOReportData.getInvestorSummaryData();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementGSOReport> unformattedPartnerStatementGSO =
                new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementGSOReportData.getPartnerStatementGSOFundInfo().get(0)), message),
                        Arrays.asList(partnerStatementGSOReportData), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementGSO = dataFormatter.format(reportStatementId, fundId, parameters, unformattedPartnerStatementGSO, csvExporter, csvExportPath);
        return formattedPartnerStatementGSO;
    }

    @RequestMapping(value = "/raw/partner-statement/gso", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementGSOReport> getUnFormattedPartnerStatementGSOReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("CALC_INCEP_ROR") int calcIncepROR,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam(value = "SUMMARY_FUNDS", required = false) String summaryFunds,
            @RequestParam("SORT_ORDER_PS") int sortOrder,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {

        PartnerStatementGSOReport partnerStatementGSOReport = partnerStatementGSOLocalService.getPartnerStatementGSO(fundId, yearDate, previousDate, statementDate, fromHid, toHid, useXref,
                fundSummary, calcIncepROR, firstNameFirst, signatureInfo, summaryFunds, sortOrder, fundToDateInfo, investorLevel, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        PortissUnFormattedReport<PartnerStatementGSOReport> unformattedPartnerStatementGSO =
                new PortissUnFormattedReport<>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null),
                        Arrays.asList(partnerStatementGSOReport), Collections.emptyList());
        return unformattedPartnerStatementGSO;
    }

    private PortissReportHeader getReportHeader(FundDto fundDto, Optional<FundInfo> optionalPartnerStatementGSOFundInfo, String waterMarkMessage) {
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundDto.getFundId()), "JASPER_PARTNER_STMT");
        Map<String, Object> userObjects;
        if (optionalPartnerStatementGSOFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementGSOFundInfo", optionalPartnerStatementGSOFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }

        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));

        return new PortissReportHeader(entityDto, userObjects, waterMarkMessage, maskedFileName, optionalPartnerStatementGSOFundInfo.get(), null);
    }

    private String getNavMessage(EntityKey<Fund> fundKey, Date navDate, int clientId) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, navDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), clientId, 0);
        return navMessage.isPresent() ? navMessage.get() : "";
    }

}
