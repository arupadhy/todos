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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.pinv.PartnerStatementInvReport;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.EntityDto;
import com.imsi.iss.portiss.rmi.dto.FundDto;
import com.imsi.iss.portiss.rmi.dto.FundInfo;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.local.MonthEndNavService;
import com.imsi.iss.portiss.server.service.local.ReportService;
import com.imsi.iss.portiss.storedproc.service.PartnerStatementInvLocalService;

@RestController
public class InvPartnerStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvPartnerStatementController.class);
    public final PartnerStatementInvLocalService partnerStatementInvLocalService;
    private final MonthEndNavService monthEndNavService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;
    private final CsvExporter csvExporter;
    private final ReportService reportService;

    @Autowired
    public InvPartnerStatementController(PartnerStatementInvLocalService partnerStatementInvLocalService, MonthEndNavService monthEndNavService, DataFormatter dataFormatter, FundService fundService,
            CsvExporter csvExporter, ReportService reportService) {
        this.partnerStatementInvLocalService = partnerStatementInvLocalService;
        this.monthEndNavService = monthEndNavService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
        this.csvExporter = csvExporter;
        this.reportService = reportService;
    }

    @RequestMapping(value = "/raw/partner-statement/inv", method = RequestMethod.POST)
    public PortissUnFormattedReport<PartnerStatementInvReport> getUnformattedPartnerStatementInvReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date StmtDate,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("BAL_ONLY") int balOnly,
            @RequestParam("TRANS_ONLY") int transOnly,
            @RequestParam("GSFAX") int gsfax,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("BAL_DETAILS") int balDetails,
            @RequestParam("SORT") int sort,
            @RequestParam("METHOD") int method,
            @RequestParam("NO_CONTACTS") int noContacts,
            @RequestParam("PARMSAGAMORE") int parmsagamore,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT_ORDER") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("GSAS") int gsas) {

        PartnerStatementInvReport partnerStatementInvReport = partnerStatementInvLocalService.getPartnerStatmentInvReport(fundId, previousDate, StmtDate, yearDate, fromHid, toHid, balOnly, transOnly, gsfax, parmmail, parmemail, parmfax, parmother, parmother2, parmregaddr, balDetails, sort, method, noContacts, parmsagamore, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, firstNameFirst, investorLevel, gsas, null);

        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_PARTNER_STMT");
        return new PortissUnFormattedReport<PartnerStatementInvReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(partnerStatementInvReport), Collections.emptyList());
    }

    @PojoUsed(PartnerStatementInvReport.class)
    @RequestMapping(value = "/partner-statement/inv", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedPartnerStatementInvReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("PREVIOUS_DATE") Date previousDate,
            @RequestParam("STATEMENT_DATE") Date StmtDate,
            @RequestParam("YEAR_DATE") Date yearDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("BAL_ONLY") int balOnly,
            @RequestParam("TRANS_ONLY") int transOnly,
            @RequestParam("GSFAX") int gsfax,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("BAL_DETAILS") int balDetails,
            @RequestParam("SORT") int sort,
            @RequestParam("METHOD") int method,
            @RequestParam("NO_CONTACTS") int noContacts,
            @RequestParam("PARMSAGAMORE") int parmsagamore,
            @RequestParam("FUND_SUMMARY") int fundSummary,
            @RequestParam("SUMMARY_FUNDS") String summaryFunds,
            @RequestParam("FUND_TO_DATE_INFO") int fundToDateInfo,
            @RequestParam("SORT_ORDER") int sortOrder,
            @RequestParam("SIGNATURE_INFO") int signatureInfo,
            @RequestParam("FIRST_NAME_FIRST") int firstNameFirst,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("GSAS") int gsas,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("fundId", fundId);
        parameters.put("previousDate", previousDate);
        parameters.put("StmtDate", StmtDate);
        parameters.put("yearDate", yearDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("balOnly", balOnly);
        parameters.put("transOnly", transOnly);
        parameters.put("gsfax", gsfax);
        parameters.put("parmmail", parmmail);
        parameters.put("parmemail", parmemail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("balDetails", balDetails);
        parameters.put("sort", sort);
        parameters.put("method", method);
        parameters.put("noContacts", noContacts);
        parameters.put("parmsagamore", parmsagamore);
        parameters.put("fundSummary", fundSummary);
        parameters.put("summaryFunds", summaryFunds);
        parameters.put("fundToDateInfo", fundToDateInfo);
        parameters.put("sortOrder", sortOrder);
        parameters.put("signatureInfo", signatureInfo);
        parameters.put("firstNameFirst", firstNameFirst);
        parameters.put("investorLevel", investorLevel);
        parameters.put("gsas", gsas);
        parameters.put("csvExportPath", csvExportPath);

        PartnerStatementInvReport partnerStatementInvReport = partnerStatementInvLocalService.getPartnerStatmentInvReport(fundId, previousDate, StmtDate, yearDate, fromHid, toHid, balOnly, transOnly, gsfax, parmmail, parmemail, parmfax, parmother, parmother2, parmregaddr, balDetails, sort, method, noContacts, parmsagamore, fundSummary, summaryFunds, fundToDateInfo, sortOrder, signatureInfo, firstNameFirst, investorLevel, gsas, csvType);

        List<FundInfo> partnerStatementInvFundInfoList = partnerStatementInvReport.getPartnerStatementInvFundInfo();
        Optional<FundInfo> partnerStatementFundInfo = Optional.of(partnerStatementInvFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (partnerStatementFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", partnerStatementFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, StmtDate, fundDto.getClientId());
        PortissUnFormattedReport<PartnerStatementInvReport> unFormatedPartnerStatementInvReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(partnerStatementInvReport.getPartnerStatementInvFundInfo().get(0)), message), Arrays.asList(partnerStatementInvReport), Collections.emptyList());
        PortissFormattedReport formattedPartnerStatementPart = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedPartnerStatementInvReport, csvExporter, csvExportPath);
        return formattedPartnerStatementPart;
    }

    private PortissReportHeader getReportHeader(FundDto fundDto, Optional<FundInfo> optionPartnerStatementInvFundInfo, String waterMarkMessage) {
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundDto.getFundId()), "JASPER_PARTNER_STMT");
        Map<String, Object> userObjects;
        if (optionPartnerStatementInvFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("partnerStatementFundInfo", optionPartnerStatementInvFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }

        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));
        return new PortissReportHeader(entityDto, userObjects, waterMarkMessage, maskedFileName, optionPartnerStatementInvFundInfo.get(), null);
    }

    private String getNavMessage(EntityKey<Fund> fundKey, Date navDate, int clientId) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, navDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), clientId, 0);
        return navMessage.isPresent() ? navMessage.get() : "";
    }

}