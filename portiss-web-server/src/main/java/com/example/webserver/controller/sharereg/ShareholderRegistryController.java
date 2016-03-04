package com.example.webserver.controller.sharereg;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.sharedregistry.SharedRegistryReport;
import com.imsi.iss.portiss.rmi.api.dto.sharedregistry.SharedRegistrySortOrder;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.report.SharedRegistryLocalService;

@RestController
public class ShareholderRegistryController {

    private final SharedRegistryLocalService sharedRegistryLocalService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;
    private final CsvExporter csvExporter;

    @Autowired
    public ShareholderRegistryController(SharedRegistryLocalService sharedRegistryLocalService, DataFormatter dataFormatter, FundService fundService, CsvExporter csvExporter) {
        this.sharedRegistryLocalService = sharedRegistryLocalService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
        this.csvExporter = csvExporter;
    }

    @RequestMapping(value = "/raw/shareholder-registry", method = RequestMethod.POST)
    public PortissUnFormattedReport<SharedRegistryReport> getUnFormattedSharedRegistryReport(@RequestParam("FUND_ID") long fundId,
            @RequestParam("BALANCE_DATE") LocalDate balanceDate,
            @RequestParam("SORT_ORDER") String sortOrder,
            @RequestParam("AFTER_CASH_ONLY_REDEMPTIONS") boolean afterCashOnlyRedemptions,
            @RequestParam("SHOW_AFTER_TRANSACTIONS") boolean showAfterTransactions,
            @RequestParam("EXCLUDE_INVESTORS") boolean excludeInvestors,
            @RequestParam("HIGHLIGHT_NEW_INVESTORS") boolean highlightNewInvestors,
            @RequestParam("SHOW_SORT_CATEGORY_SUMMARY") boolean showSortCategorySummary,
            @RequestParam("CHANGE_VALUE_TO_BASE_CURRENCY") boolean changeValueToBaseCurrency,
            @RequestParam("LOT_LEVEL_DETAILS") boolean showLotLevelDetails) {

        SharedRegistryReport sharedRegistryReport = sharedRegistryLocalService.getSharedRegistryDetails(fundId,
                balanceDate,
                SharedRegistrySortOrder.valueOf(sortOrder), afterCashOnlyRedemptions,
                showAfterTransactions, excludeInvestors, highlightNewInvestors, showSortCategorySummary, changeValueToBaseCurrency, showLotLevelDetails);

        PortissUnFormattedReport<SharedRegistryReport> reportData = new PortissUnFormattedReport<>(
                new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, null, null, null), Arrays.asList(sharedRegistryReport),
                Collections.<InvestorSummary> emptyList());

        return reportData;
    }

    @PojoUsed(SharedRegistryReport.class)
    @RequestMapping(value = "/shareholder-registry", method = RequestMethod.POST)
    public PortissFormattedReport getSharedRegistryReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("BALANCE_DATE") LocalDate balanceDate,
            @RequestParam("SORT_ORDER") String sortOrder,
            @RequestParam("AFTER_CASH_ONLY_REDEMPTIONS") boolean afterCashOnlyRedemptions,
            @RequestParam("SHOW_AFTER_TRANSACTIONS") boolean showAfterTransactions,
            @RequestParam("EXCLUDE_INVESTORS") boolean excludeInvestors,
            @RequestParam("HIGHLIGHT_NEW_INVESTORS") boolean highlightNewInvestors,
            @RequestParam("SHOW_SORT_CATEGORY_SUMMARY") boolean showSortCategorySummary,
            @RequestParam("CHANGE_VALUE_TO_BASE_CURRENCY") boolean changeValueToBaseCurrency,
            @RequestParam("LOT_LEVEL_DETAILS") boolean showLotLevelDetails,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {

        SharedRegistryReport sharedRegistryReport = sharedRegistryLocalService.getSharedRegistryDetails(fundId,
                balanceDate,
                SharedRegistrySortOrder.valueOf(sortOrder), afterCashOnlyRedemptions,
                showAfterTransactions, excludeInvestors, highlightNewInvestors, showSortCategorySummary, changeValueToBaseCurrency, showLotLevelDetails);

        // invoke formatter
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("fundId", fundId);
        parameters.put("balanceDate", balanceDate);
        parameters.put("sortOrder", sortOrder);
        parameters.put("afterCashOnlyRedemptions", afterCashOnlyRedemptions);
        parameters.put("showAfterTransactions", showAfterTransactions);
        parameters.put("excludeInvestors", excludeInvestors);
        parameters.put("highlightNewInvestors", highlightNewInvestors);
        //FIXME: there should be a better way to do this, as the data is present in the POJO, so why add it to the parameters?
        parameters.put("sortOrderDisplayLabel", sharedRegistryReport.getSortOrderDisplayLabel());
        parameters.put("csvExportPath", csvExportPath);
        PortissUnFormattedReport<SharedRegistryReport> reportData = new PortissUnFormattedReport<>(
                new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, null, null, null), Arrays.asList(sharedRegistryReport),
                Collections.<InvestorSummary> emptyList());
        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);

        return portissReport;
    }

}
