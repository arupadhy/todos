package com.example.webserver.controller.subscriptionandredemption;

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

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.subsreds.SubscriptionAndRedemptionDetails;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.local.MonthEndNavService;
import com.imsi.iss.portiss.storedproc.service.SubscriptionAndRedemptionLocalService;

@RestController
public class SubscriptionAndRedemptionController {
    private final SubscriptionAndRedemptionLocalService subscriptionAndRedemptionLocalService;
    private final FundService fundService;
    private final DataFormatter dataFormatter;
    private final CsvExporter csvExporter;
    private final MonthEndNavService monthEndNavService;

    @Autowired
    public SubscriptionAndRedemptionController(SubscriptionAndRedemptionLocalService subscriptionAndRedemptionLocalService, FundService fundService, DataFormatter dataFormatter,
            CsvExporter csvExporter, MonthEndNavService monthEndNavService) {
        this.subscriptionAndRedemptionLocalService = subscriptionAndRedemptionLocalService;
        this.fundService = fundService;
        this.dataFormatter = dataFormatter;
        this.csvExporter = csvExporter;
        this.monthEndNavService = monthEndNavService;
    }

    @RequestMapping(value = "/raw/subscription-and-redemption/sos", method = RequestMethod.POST)
    public PortissUnFormattedReport<SubscriptionAndRedemptionDetails> getUnformattedSubscriptionAndRedemptionSosReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("ID_TYPE") int idType,
            @RequestParam("DATE_FROM") Date dateFrom,
            @RequestParam("DATE_TO") Date dateTo,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("DROPDOWN") String dropdown,
            @RequestParam("PARM_TYPE") int parmType,
            @RequestParam("CALC_LEVEL") String calcLevel,
            @RequestParam("CASHONLY") int cashonly,
            @RequestParam("NONCASHONLY") int noncashonly,
            @RequestParam("RBCINVESTOR") int rbcinvestor,
            @RequestParam("USE_AS_SUBPROC") int useAsSubproc,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv) {

        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        List<SubscriptionAndRedemptionDetails> mainReportData = subscriptionAndRedemptionLocalService.getSubscriptionAndRedemptionDetails(fundKey, idType, dateFrom, dateTo, fromHid, toHid, sortOrder, dropdown, parmType, calcLevel, cashonly, noncashonly, rbcinvestor, useAsSubproc, specialLogic, excludeFlaggedInv);
        PortissUnFormattedReport<SubscriptionAndRedemptionDetails> unFormattedReport = new PortissUnFormattedReport<SubscriptionAndRedemptionDetails>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), idType), null, null, null, null, null), mainReportData, Collections.<InvestorSummary> emptyList());
        return unFormattedReport;
    }

    @PojoUsed(SubscriptionAndRedemptionDetails.class)
    @RequestMapping(value = "/subscription-and-redemption/sos", method = RequestMethod.POST)
    public PortissFormattedReport getSubscriptionAndRedemptionSosReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("ID_TYPE") int idType,
            @RequestParam("DATE_FROM") Date dateFrom,
            @RequestParam("DATE_TO") Date dateTo,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("SORT") int sortOrder,
            @RequestParam("DROPDOWN") String dropdown,
            @RequestParam("PARM_TYPE") int parmType,
            @RequestParam("CALC_LEVEL") String calcLevel,
            @RequestParam("CASHONLY") int cashonly,
            @RequestParam("NONCASHONLY") int noncashonly,
            @RequestParam("RBCINVESTOR") int rbcinvestor,
            @RequestParam("USE_AS_SUBPROC") int useAsSubproc,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("idType", idType);
        parameters.put("dateFrom", dateFrom);
        parameters.put("dateTo", dateTo);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("sortOrder", sortOrder);
        parameters.put("dropdown", dropdown);
        parameters.put("parmType", parmType);
        parameters.put("calcLevel", calcLevel);
        parameters.put("cashonly", cashonly);
        parameters.put("noncashonly", noncashonly);
        parameters.put("rbcinvestor", rbcinvestor);
        parameters.put("useAsSubproc", useAsSubproc);
        parameters.put("specialLogic", specialLogic);
        parameters.put("excludeFlaggedInv", excludeFlaggedInv);
        parameters.put("csvExportPath", csvExportPath);

        PortissUnFormattedReport<SubscriptionAndRedemptionDetails> reportData = getUnformattedSubscriptionAndRedemptionSosReport(fundId, idType, dateFrom, dateTo, fromHid, toHid, sortOrder, dropdown, parmType, calcLevel, cashonly, noncashonly, rbcinvestor, useAsSubproc, specialLogic, excludeFlaggedInv);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);

        return portissReport;
    }
}
