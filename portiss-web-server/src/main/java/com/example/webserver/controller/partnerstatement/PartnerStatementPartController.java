package com.example.webserver.controller.partnerstatement;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartAddlActivityReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartBainReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartBenchmarkReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartCanyonReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartDiscoveryReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartElkhornReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartEqmcBrevanReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartLehmanReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartLonePineReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartLpReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartOzReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartPaulsonEquityReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartSearockReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartSitInvReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartStmtDiscoveryLpReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartStmtNewBrookReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartTaconicReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartVisiumReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartYtdConsolidatedReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.ppart.PartnerStatementPartYtdReport;
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
import com.imsi.iss.portiss.storedproc.service.PartnerStatementPartLocalService;

@RestController
public class PartnerStatementPartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerStatementPartController.class);
    private final PartnerStatementPartLocalService partnerStatementPartLocalService;
    private final MonthEndNavService monthEndNavService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;
    private final CsvExporter csvExporter;
    private final ReportService reportService;

    @Autowired
    public PartnerStatementPartController(PartnerStatementPartLocalService partnerStatementPartLocalService, MonthEndNavService monthEndNavService, DataFormatter dataFormatter,
            FundService fundService, @Qualifier("partnerStatementCsvExporter") CsvExporter csvExporter, ReportService reportService) {
        this.partnerStatementPartLocalService = partnerStatementPartLocalService;
        this.monthEndNavService = monthEndNavService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
        this.csvExporter = csvExporter;
        this.reportService = reportService;
    }

    @PojoUsed(PartnerStatementPartReport.class)
    @RequestMapping(value = "/partner-statement/part", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("ISIN_1_LOGIC") int isin1Logic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("GET_TRANSFER") int getTransfer,
            @RequestParam("HIDE_GP_INVESTOR") int hideGpInvestors,
            @RequestParam("ADD_ADMIN_REP") int addAdminRep,
            @RequestParam("PARMOTHER3") int parmother3,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("estimateLetter", estimateLetter);
        parameters.put("specialLogic", specialLogic);
        parameters.put("alwaysShowIncep", alwaysShowIncep);
        parameters.put("testrun", testrun);
        parameters.put("overrideInv", overrideInv);
        parameters.put("footnoteLogic", footnoteLogic);
        parameters.put("isin1Logic", isin1Logic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("getTransfer", getTransfer);
        parameters.put("hideGpInvestors", hideGpInvestors);
        parameters.put("addAdminRep", addAdminRep);
        parameters.put("parmother3", parmother3);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, footnoteLogic, isin1Logic, specialLogic2, getTransfer, hideGpInvestors, addAdminRep, parmother3, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartReport> getUnformattedPartnerStatementPartReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam("QUARTER_DATE") LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("ISIN_1_LOGIC") int isin1Logic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("GET_TRANSFER") int getTransfer,
            @RequestParam("HIDE_GP_INVESTOR") int hideGpInvestors,
            @RequestParam("ADD_ADMIN_REP") int addAdminRep,
            @RequestParam("PARMOTHER3") int parmother3) {

        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, footnoteLogic, isin1Logic, specialLogic2, getTransfer, hideGpInvestors, addAdminRep, parmother3, null);

        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());
    }

    private PortissReportHeader getReportHeader(FundDto fundDto, Optional<FundInfo> optionPartnerStatementPartFundInfo, String waterMarkMessage) {
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundDto.getFundId()), "JASPER_PARTNER_STMT");
        Map<String, Object> userObjects;
        if (optionPartnerStatementPartFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", optionPartnerStatementPartFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));
        return new PortissReportHeader(entityDto, userObjects, waterMarkMessage, maskedFileName, optionPartnerStatementPartFundInfo.get(), null);
    }

    private String getNavMessage(EntityKey<Fund> fundKey, LocalDate navDate, int clientId) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, navDate, clientId, 0);
        return navMessage.isPresent() ? navMessage.get() : "";
    }

    @PojoUsed(PartnerStatementPartDiscoveryReport.class)
    @RequestMapping(value = "/partner-statement/part/discovery", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartDiscoveryReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("SHOW_CLASS_DATA") int showClassData,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam("FTP_ALL") int ftpAll,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("ADMIN_INFO") int adminInfo,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("estimateLetter", estimateLetter);
        parameters.put("specialLogic", specialLogic);
        parameters.put("alwaysShowIncep", alwaysShowIncep);
        parameters.put("testrun", testrun);
        parameters.put("overrideInv", overrideInv);
        parameters.put("showClassData", showClassData);
        parameters.put("combineResults", combineResults);
        parameters.put("ftpAll", ftpAll);
        parameters.put("loadRssplit", loadRssplit);
        parameters.put("adminInfo", adminInfo);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartDiscoveryReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartDiscoveryReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, showClassData, combineResults, ftpAll, loadRssplit, adminInfo, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartDiscovery();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getInvestorSummaryData();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartDiscoveryReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartDiscovery().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/discovery", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartDiscoveryReport> getUnformattedPartnerStatementPartDiscoveryReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("SHOW_CLASS_DATA") int showClassData,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam("FTP_ALL") int ftpAll,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("ADMIN_INFO") int adminInfo) {
        PartnerStatementPartDiscoveryReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartDiscoveryReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, showClassData, combineResults, ftpAll, loadRssplit, adminInfo, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartDiscoveryReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());
    }

    @PojoUsed(PartnerStatementPartReport.class)
    @RequestMapping(value = "/partner-statement/part/div-activity", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartDivActivityReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SHOW_WITHDRAW_FEES") int showWithdrawFees,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam("FTP_ALL") int ftpAll,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("SHOW_EXCH_AS_SUB_RED") int showExchAsSubRed,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("estimateLetter", estimateLetter);
        parameters.put("showWithdrawFees", showWithdrawFees);
        parameters.put("specialLogic", specialLogic);
        parameters.put("combineResults", combineResults);
        parameters.put("ftpAll", ftpAll);
        parameters.put("loadRssplit", loadRssplit);
        parameters.put("showExchAsSubRed", showExchAsSubRed);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartDivActivityReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, showWithdrawFees, specialLogic, combineResults, ftpAll, loadRssplit, showExchAsSubRed, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/div-activity", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartReport> getUnformattedPartnerStatementPartDivActivityReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SHOW_WITHDRAW_FEES") int showWithdrawFees,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam("FTP_ALL") int ftpAll,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("SHOW_EXCH_AS_SUB_RED") int showExchAsSubRed) {
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartDivActivityReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, showWithdrawFees, specialLogic, combineResults, ftpAll, loadRssplit, showExchAsSubRed, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartReport.class)
    @RequestMapping(value = "/partner-statement/part/golden-tree", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartGoldenTreeReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("alwaysShowIncep", alwaysShowIncep);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartGoldenTreeReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, alwaysShowIncep, specialLogic, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/golden-tree", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartReport> getUnformattedPartnerStatementPartGoldenTreeReport(@RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartGoldenTreeReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, alwaysShowIncep, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartReport.class)
    @RequestMapping(value = "/partner-statement/part/trian", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartTrian(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("OVERRIDE_GROUPBY") int overrideGroupby,
            @RequestParam("FINAL_SORT_ORDER") int finalSortOrder,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("specialLogic", specialLogic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("overrideGroupby", overrideGroupby);
        parameters.put("finalSortOrder", finalSortOrder);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartTrianReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, specialLogic, specialLogic2, useProceedsEntered, overrideGroupby, finalSortOrder, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/trian", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartReport> getUnformattedPartnerStatementPartTrian(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("OVERRIDE_GROUPBY") int overrideGroupby,
            @RequestParam("FINAL_SORT_ORDER") int finalSortOrder) {
        PartnerStatementPartReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartTrianReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, specialLogic, specialLogic2, useProceedsEntered, overrideGroupby, finalSortOrder, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartSitInvReport.class)
    @RequestMapping(value = "/partner-statement/part/sit-inv", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartSitInv(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("specialLogic", specialLogic);
        parameters.put("combineResults", combineResults);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartSitInvReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartSitInvReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, specialLogic, combineResults, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartSitInvReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/sit-inv", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartSitInvReport> getUnformattedPartnerStatementPartSitInv(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("COMBINE_RESULTS") int combineResults) {
        PartnerStatementPartSitInvReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatmentPartSitInvReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, specialLogic, combineResults, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartSitInvReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartYtdReport.class)
    @RequestMapping(value = "/partner-statement/part/ytd", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartYtd(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_INVESTOR_LEVEL") int showInvestorLevel,
            @RequestParam("CALC_INCENT") int calcIncent,
            @RequestParam("CALC_MGT") int calcMgt,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("SUPPLEMENT") int supplement,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("specialLogic", specialLogic);
        parameters.put("showInvestorLevel", showInvestorLevel);
        parameters.put("calcIncent", calcIncent);
        parameters.put("calcMgt", calcMgt);
        parameters.put("loadRssplit", loadRssplit);
        parameters.put("supplement", supplement);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartYtdReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartYtdReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, specialLogic, showInvestorLevel, calcIncent, calcMgt, loadRssplit, supplement, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartYtdReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/ytd", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartYtdReport> getUnformattedPartnerStatementPartYtd(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_INVESTOR_LEVEL") int showInvestorLevel,
            @RequestParam("CALC_INCENT") int calcIncent,
            @RequestParam("CALC_MGT") int calcMgt,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("SUPPLEMENT") int supplement) {
        PartnerStatementPartYtdReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartYtdReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, specialLogic, showInvestorLevel, calcIncent, calcMgt, loadRssplit, supplement, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartYtdReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartPaulsonEquityReport.class)
    @RequestMapping(value = "/partner-statement/part/paulson", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartPaulson(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("SERIES_INFO") int seriesInfo,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("seriesInfo", seriesInfo);
        parameters.put("investorLevel", investorLevel);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartPaulsonEquityReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartPaulsonEquityReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, seriesInfo, investorLevel, specialLogic, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartPaulsonEquityReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/paulson", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartPaulsonEquityReport> getUnformattedPartnerStatementPartPaulson(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("SERIES_INFO") int seriesInfo,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartPaulsonEquityReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartPaulsonEquityReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, seriesInfo, investorLevel, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartPaulsonEquityReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartLehmanReport.class)
    @RequestMapping(value = "/partner-statement/part/lehman", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartLehman(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("showDividends", showDividends);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartLehmanReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartLehmanReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, showDividends, specialLogic, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartLehmanReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/lehman", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartLehmanReport> getUnformattedPartnerStatementPartLehman(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartLehmanReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartLehmanReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, showDividends, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartLehmanReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartAddlActivityReport.class)
    @RequestMapping(value = "/partner-statement/part/addl-activity", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartAddlActivity(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("ADDL_ACTIVITY") int addlActivity,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("YTD_ROR_OVERRIDE") int ytdRorOverride,
            @RequestParam("OVERRIDE_GROUP") String overrideGroup,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("addlActivity", addlActivity);
        parameters.put("showDividends", showDividends);
        parameters.put("ytdRorOverride", ytdRorOverride);
        parameters.put("overrideGroup", overrideGroup);
        parameters.put("specialLogic", specialLogic);
        parameters.put("loadRssplit", loadRssplit);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartAddlActivityReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartAddlActivityReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, addlActivity, showDividends, ytdRorOverride, overrideGroup, specialLogic, loadRssplit, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartAddlActivityReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/addl-activity", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartAddlActivityReport> getUnformattedPartnerStatementPartAddlActivity(@RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("ADDL_ACTIVITY") int addlActivity,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("YTD_ROR_OVERRIDE") int ytdRorOverride,
            @RequestParam("OVERRIDE_GROUP") String overrideGroup,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit) {
        PartnerStatementPartAddlActivityReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartAddlActivityReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, addlActivity, showDividends, ytdRorOverride, overrideGroup, specialLogic, loadRssplit, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartAddlActivityReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartBenchmarkReport.class)
    @RequestMapping(value = "/partner-statement/part/benchmark", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartBenchmark(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_FN_BRACKETS") int useFnBrackets,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useFnBrackets", useFnBrackets);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartBenchmarkReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartBenchmarkReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useFnBrackets, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartBenchmarkReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/benchmark", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartBenchmarkReport> getUnformattedPartnerStatementPartBenchmark(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_FN_BRACKETS") int useFnBrackets) {
        PartnerStatementPartBenchmarkReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartBenchmarkReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useFnBrackets, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartBenchmarkReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartElkhornReport.class)
    @RequestMapping(value = "/partner-statement/part/elkhorn", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartElkhorn(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("SHOW_SHARES") int showShares,
            @RequestParam("PNL_FEE_INFO") int pnlFeeInfo,
            @RequestParam("OMNI_STYLE") int omniStyle,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("USE_GNAV") int useGnav,
            @RequestParam("SST_RHINO") int sstRhino,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("showShares", showShares);
        parameters.put("pnlFeeInfo", pnlFeeInfo);
        parameters.put("omniStyle", omniStyle);
        parameters.put("showDividents", showDividends);
        parameters.put("footnotrLogic", footnoteLogic);
        parameters.put("useGnav", useGnav);
        parameters.put("sstRhino", sstRhino);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartElkhornReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartElkhornReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, showShares, pnlFeeInfo, omniStyle, showDividends, footnoteLogic, useGnav, sstRhino, specialLogic, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartElkhornReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/elkhorn", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartElkhornReport> getUnformattedPartnerStatementPartElkhorn(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("SHOW_SHARES") int showShares,
            @RequestParam("PNL_FEE_INFO") int pnlFeeInfo,
            @RequestParam("OMNI_STYLE") int omniStyle,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("USE_GNAV") int useGnav,
            @RequestParam("SST_RHINO") int sstRhino,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartElkhornReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartElkhornReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, showShares, pnlFeeInfo, omniStyle, showDividends, footnoteLogic, useGnav, sstRhino, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartElkhornReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartVisiumReport.class)
    @RequestMapping(value = "/partner-statement/part/visium", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartVisium(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SUB_TPLUS_ONE") int subTplusOne,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("subTplusOne", subTplusOne);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartVisiumReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartVisisumReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, subTplusOne, specialLogic, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartVisiumReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/visium", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartVisiumReport> getUnformattedPartnerStatementPartVisium(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SUB_TPLUS_ONE") int subTplusOne,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartVisiumReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartVisisumReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, subTplusOne, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartVisiumReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartYtdConsolidatedReport.class)
    @RequestMapping(value = "/partner-statement/part/ytd-consolidated", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartYtdConsolidated(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("PNL_SHORT_CODE1") String pnlShortCode1,
            @RequestParam("PNL_SHORT_CODE2") String pnlShortCode2,
            @RequestParam("PNL_SHORT_CODE3") String pnlShortCode3,
            @RequestParam("PNL_SHORT_CODE4") String pnlShortCode4,
            @RequestParam("PNL_SHORT_CODE5") String pnlShortCode5,
            @RequestParam("INCLUDE_GP") int includeGp,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("pnlShortCode1", pnlShortCode1);
        parameters.put("pnlShortCode2", pnlShortCode2);
        parameters.put("pnlShortCode3", pnlShortCode3);
        parameters.put("pnlShortCode4", pnlShortCode4);
        parameters.put("pnlShortCode5", pnlShortCode5);
        parameters.put("includeGp", includeGp);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartYtdConsolidatedReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartYtdConsolidatedReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, pnlShortCode1, pnlShortCode2, pnlShortCode3, pnlShortCode4, pnlShortCode5, includeGp, specialLogic, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartYtdConsolidatedReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/ytd-consolidated", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartYtdConsolidatedReport> getUnformattedPartnerStatementPartYtdConsolidated(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("PNL_SHORT_CODE1") String pnlShortCode1,
            @RequestParam("PNL_SHORT_CODE2") String pnlShortCode2,
            @RequestParam("PNL_SHORT_CODE3") String pnlShortCode3,
            @RequestParam("PNL_SHORT_CODE4") String pnlShortCode4,
            @RequestParam("PNL_SHORT_CODE5") String pnlShortCode5,
            @RequestParam("INCLUDE_GP") int includeGp,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartYtdConsolidatedReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartYtdConsolidatedReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, pnlShortCode1, pnlShortCode2, pnlShortCode3, pnlShortCode4, pnlShortCode5, includeGp, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartYtdConsolidatedReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartTaconicReport.class)
    @RequestMapping(value = "/partner-statement/part/taconic", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartTaconicReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("YTD_ROR_OVERRIDE") int ytdRorOverride,
            @RequestParam("OVERRIDE_GROUP") String overrideGroup,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("showDividends", showDividends);
        parameters.put("ytdRorOverride", ytdRorOverride);
        parameters.put("overrideGroup", overrideGroup);
        parameters.put("specialLogic", specialLogic);
        parameters.put("combineResults", combineResults);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartTaconicReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartTaconicReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, showDividends, ytdRorOverride, overrideGroup, specialLogic, combineResults, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartTaconicReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/taconic", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartTaconicReport> getUnformattedPartnerStatementPartTaconicReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("YTD_ROR_OVERRIDE") int ytdRorOverride,
            @RequestParam("OVERRIDE_GROUP") String overrideGroup,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("COMBINE_RESULTS") int combineResults) {
        PartnerStatementPartTaconicReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartTaconicReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, showDividends, ytdRorOverride, overrideGroup, specialLogic, combineResults, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartTaconicReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartSearockReport.class)
    @RequestMapping(value = "/partner-statement/part/ytd-searock", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartYtdSearock(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("SHOW_ACTIVITY") int showActivity,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("showActivity", showActivity);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartSearockReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartYtdSearockReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, showActivity, specialLogic, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartSearockReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/ytd-searock", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartSearockReport> getUnformattedPartnerStatementPartYtdSearock(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("SHOW_ACTIVITY") int showActivity,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartSearockReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartYtdSearockReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, showActivity, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartSearockReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartStmtNewBrookReport.class)
    @RequestMapping(value = "/partner-statement/part/newbrook", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartStmtNewBrookReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("ISIN_1_LOGIC") int isin1Logic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("GET_TRANSFER") int getTransfers,
            @RequestParam("HIDE_GP_INVESTOR") int hideGpInvestors,
            @RequestParam("ADD_ADMIN_REP") int addAdminRep,
            @RequestParam("PARMOTHER3") int parmother3,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("estimateLetter", estimateLetter);
        parameters.put("specialLogic", specialLogic);
        parameters.put("alwaysShowIncep", alwaysShowIncep);
        parameters.put("testrun", testrun);
        parameters.put("overrideInv", overrideInv);
        parameters.put("footnoteLogic", footnoteLogic);
        parameters.put("isin1Logic", isin1Logic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("getTransfers", getTransfers);
        parameters.put("hideGpInvestors", hideGpInvestors);
        parameters.put("addAdminRep", addAdminRep);
        parameters.put("parmother3", parmother3);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartStmtNewBrookReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartStmtNewBrookReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, footnoteLogic, isin1Logic, specialLogic2, getTransfers, hideGpInvestors, addAdminRep, parmother3, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartStmtNewBrookReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/newbrook", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartStmtNewBrookReport> getUnformattedPartnerStatementPartStmtNewBrookReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("ISIN_1_LOGIC") int isin1Logic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("GET_TRANSFER") int getTransfers,
            @RequestParam("HIDE_GP_INVESTOR") int hideGpInvestors,
            @RequestParam("ADD_ADMIN_REP") int addAdminRep,
            @RequestParam("PARMOTHER3") int parmother3) {
        PartnerStatementPartStmtNewBrookReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartStmtNewBrookReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, footnoteLogic, isin1Logic, specialLogic2, getTransfers, hideGpInvestors, addAdminRep, parmother3, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartStmtNewBrookReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartSearockReport.class)
    @RequestMapping(value = "/partner-statement/part/oz", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartOZReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("specialLogic", specialLogic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartOzReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartOzReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, specialLogic, specialLogic2, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartOzReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/oz", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartOzReport> getUnformattedPartnerStatementPartOZReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("SPECIALlLOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2) {
        PartnerStatementPartOzReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartOzReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, specialLogic, specialLogic2, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartOzReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartLonePineReport.class)
    @RequestMapping(value = "/partner-statement/part/lone-pine", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartLonePine(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            // @RequestParam("DEBUG_MODE") int debugMode,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("specialLogic", specialLogic);
        // parameters.put("debugMode", debugMode);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartLonePineReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartLonePineReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, specialLogic, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartLonePineReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;

    }

    @RequestMapping(value = "/raw/partner-statement/part/lone-pine", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartLonePineReport> getUnformattedPartnerStatementPartLonePine(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        PartnerStatementPartLonePineReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartLonePineReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, specialLogic, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartLonePineReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartBainReport.class)
    @RequestMapping(value = "/partner-statement/part/bain", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartBainReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("ADMIN_INFO") int adminInfo,
            @RequestParam("OVERWRITE_PERF") int overwritePerf,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("OVERRIDE_GROUPBY") int overrideGroupby,
            @RequestParam("FINAL_SORT_ORDER") int finalSortOrder,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("specialLogic", specialLogic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("adminInfo", adminInfo);
        parameters.put("overwritePerf", overwritePerf);
        parameters.put("footnoteLogic", footnoteLogic);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("overrideGroupby", overrideGroupby);
        parameters.put("finalSortOrder", finalSortOrder);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartBainReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartBainReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, specialLogic, specialLogic2, adminInfo, overwritePerf, footnoteLogic, useProceedsEntered, overrideGroupby, finalSortOrder, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartBainReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/bain", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartBainReport> getUnformattedPartnerStatementPartBainReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("ADMIN_INFO") int adminInfo,
            @RequestParam("OVERWRITE_PERF") int overwritePerf,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("OVERRIDE_GROUPBY") int overrideGroupby,
            @RequestParam("FINAL_SORT_ORDER") int finalSortOrder) {
        PartnerStatementPartBainReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartBainReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, specialLogic, specialLogic2, adminInfo, overwritePerf, footnoteLogic, useProceedsEntered, overrideGroupby, finalSortOrder, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartBainReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartCanyonReport.class)
    @RequestMapping(value = "/partner-statement/part/canyon", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartCanyonReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("NO_SP_ROR") int noSpRor,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useXref", useXref);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("estimateLetter", estimateLetter);
        parameters.put("specialLogic", specialLogic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("alwaysShowIncep", alwaysShowIncep);
        parameters.put("footnoteLogic", footnoteLogic);
        parameters.put("noSpRor", noSpRor);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartCanyonReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartCanyonReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, specialLogic, specialLogic2, alwaysShowIncep, footnoteLogic, noSpRor, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartCanyonReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/canyon", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartCanyonReport> getUnformattedPartnerStatementPartCanyonReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_XREF") int useXref,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIALLOGIC") int specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("FOOTNOTE_LOGIC") int footnoteLogic,
            @RequestParam("NO_SP_ROR") int noSpRor) {
        PartnerStatementPartCanyonReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartCanyonReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useXref, useProceedsEntered, estimateLetter, specialLogic, specialLogic2, alwaysShowIncep, footnoteLogic, noSpRor, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartCanyonReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartLpReport.class)
    @RequestMapping(value = "/partner-statement/part/lp", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartLpReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("USE_GROSS") int useGross,
            @RequestParam("USE_CLIENT_REF1") int useClientRef1,
            @RequestParam("OVERRIDE_INVESTOR") int overrideInvestor,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("specialLogic", specialLogic);
        parameters.put("showDividends", showDividends);
        parameters.put("useGross", useGross);
        parameters.put("useClientRef1", useClientRef1);
        parameters.put("overrideInvestor", overrideInvestor);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartLpReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartLpReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, specialLogic, showDividends, useGross, useClientRef1, overrideInvestor, csvType);

        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartLpReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/lp", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartLpReport> getUnformattedPartnerStatementPartLpReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_DIVIDENDS") int showDividends,
            @RequestParam("USE_GROSS") int useGross,
            @RequestParam("USE_CLIENT_REF1") int useClientRef1,
            @RequestParam("OVERRIDE_INVESTOR") int overrideInvestor)
    {

        PartnerStatementPartLpReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartLpReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, specialLogic, showDividends, useGross, useClientRef1, overrideInvestor, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartLpReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartStmtDiscoveryLpReport.class)
    @RequestMapping(value = "/partner-statement/part/discovery/lp", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartStmtDiscoveryLpReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("SHOW_CLASS_DATA") int showClassData,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam("FTP_ALL") int ftpAll,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("ADMIN_INFO") int adminInfo,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("investorLevel", investorLevel);
        parameters.put("noCompositeRor", noCompositeRor);
        parameters.put("exportOutput", exportOutput);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("showYtdRorT1Redem", showYtdRorT1Redem);
        parameters.put("weeklyEstimates", weeklyEstimates);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("estimateLetter", estimateLetter);
        parameters.put("specialLogic", specialLogic);
        parameters.put("alwaysShowIncep", alwaysShowIncep);
        parameters.put("testrun", testrun);
        parameters.put("overrideInv", overrideInv);
        parameters.put("showClassData", showClassData);
        parameters.put("combineResults", combineResults);
        parameters.put("ftpAll", ftpAll);
        parameters.put("loadRssplit", loadRssplit);
        parameters.put("adminInfo", adminInfo);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementPartStmtDiscoveryLpReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartStmtDiscoveryLpReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, showClassData, combineResults, ftpAll, loadRssplit, adminInfo, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartStmtDiscoveryLpReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/discovery/lp", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartStmtDiscoveryLpReport> getUnformattedPartnerStatementPartStmtDiscoveryLpReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("NO_COMPOSITE_ROR") int noCompositeRor,
            @RequestParam("EXPORT_OUTPUT") int exportOutput,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("SHOW_YTD_ROR_T1_REDEM") int showYtdRorT1Redem,
            @RequestParam("WEEKLY_ESTIMATES") int weeklyEstimates,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("ESTIMATE_LETTER") int estimateLetter,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("ALWAYS_SHOW_INCEP") int alwaysShowIncep,
            @RequestParam("TESTRUN") int testrun,
            @RequestParam("OVERRIDE_INV") int overrideInv,
            @RequestParam("SHOW_CLASS_DATA") int showClassData,
            @RequestParam("COMBINE_RESULTS") int combineResults,
            @RequestParam("FTP_ALL") int ftpAll,
            @RequestParam("LOAD_RSSPLIT") int loadRssplit,
            @RequestParam("ADMIN_INFO") int adminInfo) {
        PartnerStatementPartStmtDiscoveryLpReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartStmtDiscoveryLpReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmemail, parmmail, parmfax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, investorLevel, noCompositeRor, exportOutput, incomeStmt, showYtdRorT1Redem, weeklyEstimates, useProceedsEntered, estimateLetter, specialLogic, alwaysShowIncep, testrun, overrideInv, showClassData, combineResults, ftpAll, loadRssplit, adminInfo, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartStmtDiscoveryLpReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());

    }

    @PojoUsed(PartnerStatementPartEqmcBrevanReport.class)
    @RequestMapping(value = "/partner-statement/part/brevan", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementPartBrevan(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmEmail,
            @RequestParam("PARMMAIL") int parmMail,
            @RequestParam("PARMFAX") int parmFax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("SHOW_TRANCHES") int showTranches,
            @RequestParam("SUBPROC") String subProc,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("EQ_BALANCE_YTD") int eqBalanceYtd,
            @RequestParam("SUPPLEMENT") int supplement,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("yearDate", yearDate);
        parameters.put("previousDate", previousDate);
        parameters.put("quarterDate", quarterDate);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("outNavInfo", outNavInfo);
        parameters.put("parmEmail", parmEmail);
        parameters.put("parmMail", parmMail);
        parameters.put("parmFax", parmFax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("calcIncepRor", calcIncepRor);
        parameters.put("gpFilter", gpFilter);
        parameters.put("showTranches", showTranches);
        parameters.put("subProc", subProc);
        parameters.put("incomeStmt", incomeStmt);
        parameters.put("investorLevel", investorLevel);
        parameters.put("useProceedsEntered", useProceedsEntered);
        parameters.put("eqBalanceYtd", eqBalanceYtd);
        parameters.put("supplement", supplement);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("csvExportPath", csvExportPath);
        PartnerStatementPartEqmcBrevanReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartBrevanReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmEmail, parmMail, parmFax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, showTranches, subProc, incomeStmt, investorLevel, useProceedsEntered, eqBalanceYtd, supplement, specialLogic2, csvType);
        List<FundInfo> partnerStatementPartFundInfoList = partnerStatementPartReport.getPartnerStatementPartFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementPartFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = partnerStatementPartReport.getPartnerStatementPartInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementPartEqmcBrevanReport> unFormatedPartnerStatementPartReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementPartReport.getPartnerStatementPartFundInfo().get(0)), message), Arrays.asList(partnerStatementPartReport), investorSummaryData);
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementPartReport, csvExporter, csvExportPath);

        return formattedPartnerStatementPart;
    }

    @RequestMapping(value = "/raw/partner-statement/part/brevan", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementPartEqmcBrevanReport> getUnformattedPartnerStatementPartBrevan(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("YEAR_DATE") LocalDate yearDate,
            @RequestParam("PREVIOUS_DATE") LocalDate previousDate,
            @RequestParam(value = "QUARTER_DATE", required = false) LocalDate quarterDate,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("OUT_NAV_INFO") int outNavInfo,
            @RequestParam("PARMEMAIL") int parmEmail,
            @RequestParam("PARMMAIL") int parmMail,
            @RequestParam("PARMFAX") int parmFax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("CALC_INCEP_ROR") int calcIncepRor,
            @RequestParam("GP_FILTER") int gpFilter,
            @RequestParam("SHOW_TRANCHES") int showTranches,
            @RequestParam("SUBPROC") String subProc,
            @RequestParam("INCOME_STMT") int incomeStmt,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("USE_PROCEEDS_ENTERED") int useProceedsEntered,
            @RequestParam("EQ_BALANCE_YTD") int eqBalanceYtd,
            @RequestParam("SUPPLEMENT") int supplement,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2) {
        PartnerStatementPartEqmcBrevanReport partnerStatementPartReport = partnerStatementPartLocalService.getPartnerStatementPartBrevanReport(fundId, yearDate, previousDate, quarterDate, statementDate, fromHid, toHid, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, outNavInfo, parmEmail, parmMail, parmFax, parmother, parmother2, parmregaddr, firstNameFirst, calcIncepRor, gpFilter, showTranches, subProc, incomeStmt, investorLevel, useProceedsEntered, eqBalanceYtd, supplement, specialLogic2, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementPartEqmcBrevanReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementPartReport), Collections.emptyList());
    }
}