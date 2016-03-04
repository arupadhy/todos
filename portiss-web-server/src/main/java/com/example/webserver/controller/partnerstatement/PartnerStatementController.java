package com.example.webserver.controller.partnerstatement;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementFundSummary;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementOld;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.PartnerStatementReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.standard.PartnerStatementWithContact;
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
import com.imsi.iss.portiss.server.service.local.impl.partnerstatement.comparator.InvestorSummaryComparator;
import com.imsi.iss.portiss.server.service.report.PartnerStatementLocalService;
import com.imsi.iss.portiss.server.service.report.PartnerStatementLocalServiceOld;

@RestController
public class PartnerStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartnerStatementController.class);
    private final PartnerStatementLocalService partnerStatementLocalService;
    private final PartnerStatementLocalServiceOld partnerStatementLocalServiceOld;
    private final DataFormatter dataFormatter;
    private final FundService fundService;
    private final CsvExporter csvExporter;
    private final ReportService reportService;

    @Autowired
    public PartnerStatementController(PartnerStatementLocalService partnerStatementLocalService, PartnerStatementLocalServiceOld partnerStatementLocalServiceOld, DataFormatter dataFormatter,
            FundService fundService,
            @Qualifier("partnerStatementCsvExporter") CsvExporter csvExporter, ReportService reportService) {
        this.partnerStatementLocalService = partnerStatementLocalService;
        this.partnerStatementLocalServiceOld = partnerStatementLocalServiceOld;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
        this.csvExporter = csvExporter;
        this.reportService = reportService;
    }

    @PojoUsed(PartnerStatementOld.class)
    @RequestMapping(value = "/partner-statement", method = RequestMethod.POST)
    public PortissFormattedReport getPartnerStatement(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("HIDE_ZERO_BALANCE_ROWS") boolean hideZeroBalanceRows,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("hideZeroBalanceRows", hideZeroBalanceRows);
        parameters.put("csvExportPath", csvExportPath);
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        Fund fund = fundService.getFund(fundKey);

        List<PartnerStatementOld> reportData = partnerStatementLocalServiceOld.getPartnerStatement(fund, statementDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), fromHid, toHid, hideZeroBalanceRows);

        List<InvestorSummary> investorSummaries = getInvestorSummaryData(reportData, fundKey);

        PortissUnFormattedReport<PartnerStatementOld> partnerStatementReport = getPortissUnFormattedReport(fundId, reportData, Optional.empty(), investorSummaries);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, partnerStatementReport, csvExporter, csvExportPath);

        return portissReport;
    }

    @PojoUsed(PartnerStatementOld.class)
    @RequestMapping(value = "/partner-statement/class-level-ror", method = RequestMethod.POST)
    public PortissFormattedReport getPartnerStatementClassLevelRor(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("HIDE_ZERO_BALANCE_ROWS") boolean hideZeroBalanceRows,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("hideZeroBalanceRows", hideZeroBalanceRows);
        parameters.put("csvExportPath", csvExportPath);
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        Fund fund = fundService.getFund(fundKey);

        List<PartnerStatementOld> reportData = partnerStatementLocalServiceOld.getPartnerStatementClassLevelRor(fund, statementDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), fromHid, toHid, hideZeroBalanceRows);

        List<InvestorSummary> investorSummaries = getInvestorSummaryData(reportData, fundKey);

        PortissUnFormattedReport<PartnerStatementOld> partnerStatementReport = getPortissUnFormattedReport(fundId, reportData, Optional.empty(), investorSummaries);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, partnerStatementReport, csvExporter, csvExportPath);
        return portissReport;
    }

    @PojoUsed(PartnerStatementOld.class)
    @RequestMapping(value = "/partner-statement/with-summaries", method = RequestMethod.POST)
    public PortissFormattedReport getPartnerStatementWithSummaries(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("HIDE_ZERO_BALANCE_ROWS") boolean hideZeroBalanceRows,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {
        LOGGER.info("Value of csvType is " + csvType);
        LOGGER.info("Value of reportRunId is " + reportRunId);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("hideZeroBalanceRows", hideZeroBalanceRows);
        parameters.put("csvExportPath", csvExportPath);

        Fund fund = fundService.getFund(new EntityKey<Fund>(Fund.class, fundId));

        PartnerStatementReport reportData = partnerStatementLocalServiceOld.getPartnerStatementWithSummaries(fund, statementDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), fromHid, toHid, hideZeroBalanceRows);

        PartnerStatementFundSummary partnerStatementFundSummary = reportData.getPartnerStatementFundSummary();
        PortissUnFormattedReport<PartnerStatementReport> partnerStatementReport = new PortissUnFormattedReport<>(getReportHeader(fundId, Optional.of(partnerStatementFundSummary)), Arrays.asList(reportData), reportData.getInvestorSummaryData());

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, partnerStatementReport, csvExporter, csvExportPath);
        return portissReport;
    }

    @RequestMapping(value = "/raw/partner-statement", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementOld> getUnformattedPartnerStatement(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("HIDE_ZERO_BALANCE_ROWS") boolean hideZeroBalanceRows) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        Fund fund = fundService.getFund(fundKey);
        List<PartnerStatementOld> partnerStatements = partnerStatementLocalServiceOld.getPartnerStatement(fund, statementDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), fromHid, toHid, hideZeroBalanceRows);
        List<InvestorSummary> investorSummaries = getInvestorSummaryData(partnerStatements, fundKey);
        PortissUnFormattedReport<PartnerStatementOld> unformattedPartnerStatement = getPortissUnFormattedReport(fundId, partnerStatements, Optional.empty(), investorSummaries);
        return unformattedPartnerStatement;

    }

    @PojoUsed(PartnerStatementOld.class)
    @RequestMapping(value = "/partner-statement/investor-level", method = RequestMethod.POST)
    public PortissFormattedReport getPartnerStatementForInvestorLevel(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("HIDE_ZERO_BALANCE_ROWS") boolean hideZeroBalanceRows,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("previousDate", previousDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("hideZeroBalanceRows", hideZeroBalanceRows);
        parameters.put("csvExportPath", csvExportPath);

        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        Fund fund = fundService.getFund(fundKey);
        List<PartnerStatementWithContact> reportData = partnerStatementLocalService.getPartnerStatementForInvestorLevel(fund, statementDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), fromHid, toHid, hideZeroBalanceRows);

        PortissUnFormattedReport<PartnerStatementWithContact> partnerStatementReport = new PortissUnFormattedReport<PartnerStatementWithContact>(getReportHeader(fundId, Optional.empty()), reportData, Collections.<InvestorSummary> emptyList());
        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, partnerStatementReport, csvExporter, csvExportPath);
        return portissReport;
    }

    private PortissUnFormattedReport<PartnerStatementOld> getPortissUnFormattedReport(long fundId, List<PartnerStatementOld> partnerStatements,
            Optional<PartnerStatementFundSummary> optionalPartnerStatementFundSummary, List<InvestorSummary> investorSummaries) {
        return new PortissUnFormattedReport<>(getReportHeader(fundId, optionalPartnerStatementFundSummary), partnerStatements, investorSummaries);
    }

    private PortissReportHeader getReportHeader(long fundId, Optional<PartnerStatementFundSummary> optionalPartnerStatementFundSummary) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        EntityDto entityDto = new EntityDto(null, 0, Arrays.asList(fundDto));
        //TODO change the reprotType to enum
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(fundKey, "JASPER_PORTISS_PSMT");

        Map<String, Object> userObjects;
        if (optionalPartnerStatementFundSummary.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundSummary", optionalPartnerStatementFundSummary.get());
        } else {
            userObjects = Collections.emptyMap();
        }

        return new PortissReportHeader(entityDto, userObjects, null, maskedFileName, null, null);
    }

    private List<InvestorSummary> getInvestorSummaryData(List<PartnerStatementOld> partnerStatements, EntityKey<Fund> fundKey) {
        List<InvestorSummary> investorSummaries = new ArrayList<>();

        int precision = fundService.getFundDto(fundKey).getReportDecimalValue();

        Map<Integer, List<PartnerStatementOld>> partnerStatementMap = partnerStatements.parallelStream().collect(Collectors.groupingBy(PartnerStatementOld::getHid));

        for (int hid : partnerStatementMap.keySet()) {
            Map<Long, List<PartnerStatementOld>> hidContactList = partnerStatementMap.get(hid).stream().collect(Collectors.groupingBy((PartnerStatementOld partnerStatement) -> {
                return partnerStatement.getContact().getContactId();
            }));
            long firstContact = hidContactList.keySet().iterator().next();
            List<InvestorSummary> hidContactInvestorSummaries = getInvestorSummary(hidContactList.get(firstContact), precision);
            investorSummaries.addAll(hidContactInvestorSummaries);
        }

        investorSummaries.sort(new InvestorSummaryComparator());

        return investorSummaries;
    }

    private List<InvestorSummary> getInvestorSummary(List<PartnerStatementOld> partnerStatements, int precision) {

        List<InvestorSummary> investorSummaries = new ArrayList<InvestorSummary>();

        for (PartnerStatementOld partnerStatement : partnerStatements) {
            int hid = partnerStatement.getHid();
            String investorName = partnerStatement.getInvestorName();
            String currency = partnerStatement.getCurrency();

            //FIXME: probably should have transshares in this case and subtract out subscriptions
            BigDecimal beginBalance = partnerStatement.getBeginningBalance();
            BigDecimal subscriptions = partnerStatement.getSubscriptions();
            BigDecimal redemptions = partnerStatement.getRedemptions();
            BigDecimal endBalance = partnerStatement.getEndingBalance();

            BigDecimal netProfit = endBalance.subtract(beginBalance).subtract(subscriptions).subtract(redemptions);

            InvestorSummary investorSummary = new InvestorSummary(hid, investorName, beginBalance, subscriptions, redemptions, netProfit, endBalance, currency);

            investorSummaries.add(investorSummary);
        }

        return investorSummaries;
    }

}
