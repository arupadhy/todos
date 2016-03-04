package com.example.webserver.controller.sharereg;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.sharedregistry.SharedRegistrySosDetail;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.EntityDto;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.storedproc.service.SharedRegistrySosDetailLocalService;
import com.imsi.util.CalendarDate;

@RestController
public class SosDetailSharedRegistryController {

    private final SharedRegistrySosDetailLocalService sharedRegistrySosDetailLocalService;
    private final FundService fundService;
    private final DataFormatter dataFormatter;
    private final CsvExporter csvExporter;

    @Autowired
    public SosDetailSharedRegistryController(SharedRegistrySosDetailLocalService sharedRegistrySosDetailLocalService, FundService fundService, DataFormatter dataFormatter, CsvExporter csvExporter) {
        this.sharedRegistrySosDetailLocalService = sharedRegistrySosDetailLocalService;
        this.fundService = fundService;
        this.dataFormatter = dataFormatter;
        this.csvExporter = csvExporter;
    }

    @RequestMapping(value = "/raw/shareholder-registry/sos", method = RequestMethod.POST)
    public PortissUnFormattedReport<SharedRegistrySosDetail> getUnformattedSharedRegistrySosDetail(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PARMSORT") int paramSort,
            @RequestParam("SHOW_AFTER_ACTIVITY") int showAfterActivity,
            @RequestParam("SUMMARY_VERSION") int summaryVersion,
            @RequestParam("SHOW_SUMMARY") int showSummary,
            @RequestParam("RUN_AS_SUBPROC") int runAsSubproc,
            @RequestParam("WITH_TRANSACTIONS") int withtransactions,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("PCT_OWN_EXCLUDE") int pctOwnExclude,
            @RequestParam("ONLY_CASH_REDS") int onlyCashReds,
            @RequestParam("BASE_CURRENCY") int baseCurrency,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        List<SharedRegistrySosDetail> mainReportData = sharedRegistrySosDetailLocalService.getShareRegistrySosDetails(fundId, CalendarDate.fromDate(statementDate), paramSort, showAfterActivity, summaryVersion, showSummary, runAsSubproc, withtransactions, specialLogic, pctOwnExclude, onlyCashReds, baseCurrency, specialLogic2, excludeFlaggedInv);
        PortissUnFormattedReport<SharedRegistrySosDetail> portissUnFormattedReport = new PortissUnFormattedReport<SharedRegistrySosDetail>(getReportHeader(fundKey), mainReportData, null);
        return portissUnFormattedReport;
    }

    @PojoUsed(SharedRegistrySosDetail.class)
    @RequestMapping(value = "/shareholder-registry/sos", method = RequestMethod.POST)
    public PortissFormattedReport getSharedRegistrySosDetail(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PARMSORT") int paramSort,
            @RequestParam("SHOW_AFTER_ACTIVITY") int showAfterActivity,
            @RequestParam("SUMMARY_VERSION") int summaryVersion,
            @RequestParam("SHOW_SUMMARY") int showSummary,
            @RequestParam("RUN_AS_SUBPROC") int runAsSubproc,
            @RequestParam("WITH_TRANSACTIONS") int withtransactions,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("PCT_OWN_EXCLUDE") int pctOwnExclude,
            @RequestParam("ONLY_CASH_REDS") int onlyCashReds,
            @RequestParam("BASE_CURRENCY") int baseCurrency,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        PortissUnFormattedReport<SharedRegistrySosDetail> reportData = getUnformattedSharedRegistrySosDetail(fundId, statementDate, paramSort, showAfterActivity, summaryVersion, showSummary, runAsSubproc, withtransactions, specialLogic, pctOwnExclude, onlyCashReds, baseCurrency, specialLogic2, excludeFlaggedInv);
        String csvExportPath = "";
        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);
        return portissReport;
    }

    private PortissReportHeader getReportHeader(EntityKey<Fund> fundKey) {
        EntityDto entityDto = fundService.getEntityDto(fundKey, 0);
        PortissReportHeader reportHeader = new PortissReportHeader(entityDto, null, null, null, null, null);
        return reportHeader;
    }

    @RequestMapping(value = "/raw/shareholder-registry/sos/harbinger", method = RequestMethod.POST)
    public PortissUnFormattedReport<SharedRegistrySosDetail> getUnformattedSharedRegistryHarbingerSosDetail(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PARMSORT") int paramSort,
            @RequestParam("SHOW_AFTER_ACTIVITY") int showAfterActivity,
            @RequestParam("SUMMARY_VERSION") int summaryVersion,
            @RequestParam("SHOW_SUMMARY") int showSummary,
            @RequestParam("RUN_AS_SUBPROC") int runAsSubproc,
            @RequestParam("WITH_TRANSACTIONS") int withtransactions,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("PCT_OWN_EXCLUDE") int pctOwnExclude,
            @RequestParam("ONLY_CASH_REDS") int onlyCashReds,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        List<SharedRegistrySosDetail> mainReportData = sharedRegistrySosDetailLocalService.getShareRegistryHarbingerSosDetails(fundId, CalendarDate.fromDate(statementDate), paramSort, showAfterActivity, summaryVersion, showSummary, runAsSubproc, withtransactions, specialLogic, pctOwnExclude, onlyCashReds, excludeFlaggedInv);
        PortissUnFormattedReport<SharedRegistrySosDetail> portissUnFormattedReport = new PortissUnFormattedReport<SharedRegistrySosDetail>(getReportHeader(fundKey), mainReportData, null);
        return portissUnFormattedReport;
    }

    @PojoUsed(SharedRegistrySosDetail.class)
    @RequestMapping(value = "/shareholder-registry/sos/harbinger", method = RequestMethod.POST)
    public PortissFormattedReport getSharedRegistryHarbingerSosDetail(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") Date statementDate,
            @RequestParam("PARMSORT") int paramSort,
            @RequestParam("SHOW_AFTER_ACTIVITY") int showAfterActivity,
            @RequestParam("SUMMARY_VERSION") int summaryVersion,
            @RequestParam("SHOW_SUMMARY") int showSummary,
            @RequestParam("RUN_AS_SUBPROC") int runAsSubproc,
            @RequestParam("WITH_TRANSACTIONS") int withtransactions,
            @RequestParam("SPECIAL_LOGIC") int specialLogic,
            @RequestParam("PCT_OWN_EXCLUDE") int pctOwnExclude,
            @RequestParam("ONLY_CASH_REDS") int onlyCashReds,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        PortissUnFormattedReport<SharedRegistrySosDetail> reportData = getUnformattedSharedRegistryHarbingerSosDetail(fundId, statementDate, paramSort, showAfterActivity, summaryVersion, showSummary, runAsSubproc, withtransactions, specialLogic, pctOwnExclude, onlyCashReds, excludeFlaggedInv);
        String csvExportPath = "";
        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);
        return portissReport;
    }
}
