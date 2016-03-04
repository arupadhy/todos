package com.example.webserver.controller.confirmletter;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.confirmletter.ConfirmLetterFundInfo;
import com.imsi.iss.portiss.rmi.api.dto.confirmletter.ConfirmLetterReport;
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
import com.imsi.iss.portiss.server.service.local.ReportService;
import com.imsi.iss.portiss.storedproc.service.ConfirmLetterLocalService;

@RestController
public class ConfirmLetterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmLetterController.class);
    private final ConfirmLetterLocalService confirmLetterLocalService;
    private final MonthEndNavService monthEndNavService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;
    private final CsvExporter csvExporter;
    private final ReportService reportService;

    @Autowired
    public ConfirmLetterController(ConfirmLetterLocalService confirmLetterLocalService, MonthEndNavService monthEndNavService, DataFormatter dataFormatter,
            FundService fundService, CsvExporter csvExporter, ReportService reportService) {
        this.confirmLetterLocalService = confirmLetterLocalService;
        this.monthEndNavService = monthEndNavService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
        this.csvExporter = csvExporter;
        this.reportService = reportService;
    }

    @PojoUsed(ConfirmLetterReport.class)
    @RequestMapping(value = "/confirm-letter", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedConfirmLetterReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("GET_ONLY_POSTED") int getOnlyPosted,
            @RequestParam("PARMTRANS") String parmtrans,
            @RequestParam("GET_GLOBALS") int getGlobals,
            @RequestParam("JPM_STYLE") int jpmStyle,
            @RequestParam("PAYMENT_GENERATED") int paymentGenerated,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("SST_STYLE") int sstStyle,
            @RequestParam("COLLAPSE") int collapse,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("SVP") int svp,
            @RequestParam("AP") int ap,
            @RequestParam("ALLIANCE") int alliance,
            @RequestParam("IBT2") int iBT2,
            @RequestParam("BEAR_STYLE") int bearStyle,
            @RequestParam(value = "PREVIOUS_DATE", required = false) LocalDate previousDate,
            @RequestParam("GROUPBYINVESTOR") int groupbyinvestor,
            @RequestParam("FURTHER_CREDIT") int furtherCredit,
            @RequestParam("SUB_AMT_RECD") int subAmtRecd,
            @RequestParam("KEEP_TRANS_ID") int keepTransId,
            @RequestParam("MODE") int mode,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("PARMOTHER3") int parmother3,
            @RequestParam("PARMOTHER4") int parmother4,
            @RequestParam("PARMOTHER5") int parmother5,
            @RequestParam("PARMOTHER6") int parmother6,
            @RequestParam("EXCL_INVESTORS") int exclInvestors,
            @RequestParam(value = "HIDE_WATERMARK_MESSAGE", required = false) boolean waterMarkMessage,
            @RequestParam("TEMPLATE_NAME") String templateName,
            @RequestParam(value = "BATCH_ID", required = false) String batchId,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "CSV_TYPE", required = false) String csvType) {

        LOGGER.info("Value of csvType is " + csvType);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fundId", fundId);
        parameters.put("reportStatementId", reportStatementId);
        parameters.put("statementDate", statementDate);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("parmemail", parmemail);
        parameters.put("parmmail", parmmail);
        parameters.put("parmfax", parmfax);
        parameters.put("parmother", parmother);
        parameters.put("parmother2", parmother2);
        parameters.put("getOnlyPosted", getOnlyPosted);
        parameters.put("parmtrans", parmtrans);
        parameters.put("getGlobals", getGlobals);
        parameters.put("investorLevel", investorLevel);
        parameters.put("jpmStyle", jpmStyle);
        parameters.put("paymentGenerated", paymentGenerated);
        parameters.put("investorLevel", investorLevel);
        parameters.put("sstStyle", sstStyle);
        parameters.put("collapse", collapse);
        parameters.put("specialLogic", specialLogic);
        parameters.put("specialLogic2", specialLogic2);
        parameters.put("svp", svp);
        parameters.put("ap", ap);
        parameters.put("alliance", alliance);
        parameters.put("iBT2", iBT2);
        parameters.put("bearStyle", bearStyle);
        parameters.put("previousDate", previousDate);
        parameters.put("groupbyinvestor", groupbyinvestor);
        parameters.put("furtherCredit", furtherCredit);
        parameters.put("subAmtRecd", subAmtRecd);
        parameters.put("keepTransId", keepTransId);
        parameters.put("mode", mode);
        parameters.put("parmregaddr", parmregaddr);
        parameters.put("parmother3", parmother3);
        parameters.put("parmother4", parmother4);
        parameters.put("parmother5", parmother5);
        parameters.put("parmother6", parmother6);
        parameters.put("exclInvestors", exclInvestors);
        parameters.put("waterMarkMessage", waterMarkMessage);
        parameters.put("batchId", batchId);
        parameters.put("csvExportPath", csvExportPath);
        parameters.put("csvType", csvType);

        ConfirmLetterReport confirmLetterReport = confirmLetterLocalService.getConfirmLetterReport(fundId, statementDate, fromHid, toHid, parmemail, parmmail, parmfax, parmother, parmother2, getOnlyPosted, parmtrans, getGlobals, jpmStyle, paymentGenerated, investorLevel, sstStyle, collapse, specialLogic, specialLogic2, svp, ap, alliance, iBT2, bearStyle, previousDate, groupbyinvestor, furtherCredit, subAmtRecd, keepTransId, mode, parmregaddr, parmother3, parmother4, parmother5, parmother6, exclInvestors, waterMarkMessage, templateName, null);
        List<ConfirmLetterFundInfo> confirmLetterFundInfoList = confirmLetterReport.getConfirmLetterFundInfo();
        Optional<ConfirmLetterFundInfo> confirmLetterFundInfo = Optional.of(confirmLetterFundInfoList.get(0));
        Map<String, Object> userObjects;
        if (confirmLetterFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("confirmLetterFundInfo", confirmLetterFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        List<InvestorSummary> investorSummaryData = confirmLetterReport.getConfirmLetterInvestorSummary();
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());

        PortissUnFormattedReport<ConfirmLetterReport> unFormatedConfirmLetterReport = new PortissUnFormattedReport<>(getReportHeader(fundDto, Optional.of(confirmLetterReport.getConfirmLetterFundInfo().get(0)), message), Arrays.asList(confirmLetterReport), investorSummaryData);
        PortissFormattedReport formattedConfirmLetter = dataFormatter.format(reportStatementId, fundId, parameters, unFormatedConfirmLetterReport, csvExporter, csvExportPath);

        return formattedConfirmLetter;
    }

    private PortissReportHeader getReportHeader(FundDto fundDto, Optional<ConfirmLetterFundInfo> optionPartnerStatementPartFundInfo, String waterMarkMessage) {
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundDto.getFundId()), "JASPER_CONFIRM_SUBS");
        Map<String, Object> userObjects;
        if (optionPartnerStatementPartFundInfo.isPresent()) {
            userObjects = new HashMap<>();
            userObjects.put("confirmLetterFundInfo", optionPartnerStatementPartFundInfo.get());
        } else {
            userObjects = Collections.emptyMap();
        }
        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));
        return new PortissReportHeader(entityDto, userObjects, waterMarkMessage, maskedFileName, null, null);
    }

    private String getNavMessage(EntityKey<Fund> fundKey, LocalDate statementDate, int clientId) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, statementDate, clientId, 0);
        return navMessage.isPresent() ? navMessage.get() : "";
    }

    @RequestMapping(value = "/raw/confirm-letter", method = RequestMethod.POST)
    public PortissUnFormattedReport<ConfirmLetterReport> getUnFormattedConfirmLetterReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("PARMEMAIL") int parmemail,
            @RequestParam("PARMMAIL") int parmmail,
            @RequestParam("PARMFAX") int parmfax,
            @RequestParam("PARMOTHER") int parmother,
            @RequestParam("PARMOTHER2") int parmother2,
            @RequestParam("GET_ONLY_POSTED") int getOnlyPosted,
            @RequestParam("PARMTRANS") String parmtrans,
            @RequestParam("GET_GLOBALS") int getGlobals,
            @RequestParam("JPM_STYLE") int jpmStyle,
            @RequestParam("PAYMENT_GENERATED") int paymentGenerated,
            @RequestParam("INVESTOR_LEVEL") int investorLevel,
            @RequestParam("SST_STYLE") int sstStyle,
            @RequestParam("COLLAPSE") int collapse,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SPECIAL_LOGIC2") String specialLogic2,
            @RequestParam("SVP") int svp,
            @RequestParam("AP") int ap,
            @RequestParam("ALLIANCE") int alliance,
            @RequestParam("IBT2") int iBT2,
            @RequestParam("BEAR_STYLE") int bearStyle,
            @RequestParam(value = "PREVIOUS_DATE", required = false) LocalDate previousDate,
            @RequestParam("GROUPBYINVESTOR") int groupbyinvestor,
            @RequestParam("FURTHER_CREDIT") int furtherCredit,
            @RequestParam("SUB_AMT_RECD") int subAmtRecd,
            @RequestParam("KEEP_TRANS_ID") int keepTransId,
            @RequestParam("MODE") int mode,
            @RequestParam("PARMREGADDR") int parmregaddr,
            @RequestParam("PARMOTHER3") int parmother3,
            @RequestParam("PARMOTHER4") int parmother4,
            @RequestParam("PARMOTHER5") int parmother5,
            @RequestParam("PARMOTHER6") int parmother6,
            @RequestParam("EXCL_INVESTORS") int exclInvestors,
            @RequestParam(value = "HIDE_WATERMARK_MESSAGE", required = false) boolean waterMarkMessage,
            @RequestParam("TEMPLATE_NAME") String templateName) {
        ConfirmLetterReport confirmLetterReport = confirmLetterLocalService.getConfirmLetterReport(fundId, statementDate, fromHid, toHid, parmemail, parmmail, parmfax, parmother, parmother2, getOnlyPosted, parmtrans, getGlobals, jpmStyle, paymentGenerated, investorLevel, sstStyle, collapse, specialLogic, specialLogic2, svp, ap, alliance, iBT2, bearStyle, previousDate, groupbyinvestor, furtherCredit, subAmtRecd, keepTransId, mode, parmregaddr, parmother3, parmother4, parmother5, parmother6, exclInvestors, waterMarkMessage, templateName, null);
        String maskedFileName = reportService.getFtpMaskedFormatForReportStatementId(new EntityKey<Fund>(Fund.class, fundId), "JASPER_CONFIRM_SUBS");

        return new PortissUnFormattedReport<ConfirmLetterReport>(new PortissReportHeader(fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), 0), null, null, maskedFileName, null, null), Arrays.asList(confirmLetterReport), Collections.emptyList());
    }
}