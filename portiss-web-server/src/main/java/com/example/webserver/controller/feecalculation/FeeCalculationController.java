package com.example.webserver.controller.feecalculation;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.export.CsvExporter;
import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.feecalculation.FeeCalculationRows;
import com.imsi.iss.portiss.rmi.api.dto.feecalculation.IncentiveFeeDetails;
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
import com.imsi.iss.portiss.storedproc.service.FeeCalculationLocalService;

@RestController
public class FeeCalculationController {
    private final FeeCalculationLocalService feeCalculationLocalService;
    private final FundService fundService;
    private final DataFormatter dataFormatter;
    private final CsvExporter csvExporter;
    private final MonthEndNavService monthEndNavService;

    @Autowired
    public FeeCalculationController(FeeCalculationLocalService feeCalculationLocalService, FundService fundService, DataFormatter dataFormatter, CsvExporter csvExporter,
            MonthEndNavService monthEndNavService) {
        this.feeCalculationLocalService = feeCalculationLocalService;
        this.fundService = fundService;
        this.dataFormatter = dataFormatter;
        this.csvExporter = csvExporter;
        this.monthEndNavService = monthEndNavService;
    }

    @RequestMapping(value = "/raw/fee-calculation/incentive", method = RequestMethod.POST)
    public PortissUnFormattedReport<IncentiveFeeDetails> getUnformattedIncentiveFeeReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("FORMAT") int format,
            @RequestParam("MODE") int mode,
            @RequestParam("SERIES_LEVEL") int seriesLevel,
            @RequestParam("OUTPUT") int output,
            @RequestParam("NON-FEE-PAYING") int nonFeePaying,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {

        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        List<IncentiveFeeDetails> mainReportData = feeCalculationLocalService.getIncentiveFeeDetails(fundKey, statementDate, format, mode, seriesLevel, output, nonFeePaying, fromHid, toHid, specialLogic);
        PortissUnFormattedReport<IncentiveFeeDetails> unFormattedReport = new PortissUnFormattedReport<IncentiveFeeDetails>(getReportHeader(fundDto, fundKey, statementDate), mainReportData, Collections.<InvestorSummary> emptyList());
        return unFormattedReport;
    }

    @PojoUsed(IncentiveFeeDetails.class)
    @RequestMapping(value = "/fee-calculation/incentive", method = RequestMethod.POST)
    public PortissFormattedReport getIncentiveFeeReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("FORMAT") int format,
            @RequestParam("MODE") int mode,
            @RequestParam("SERIES_LEVEL") int seriesLevel,
            @RequestParam("OUTPUT") int output,
            @RequestParam("NON-FEE-PAYING") int nonFeePaying,
            @RequestParam("HID_FROM") int fromHid,
            @RequestParam("HID_TO") int toHid,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("format", format);
        parameters.put("mode", mode);
        parameters.put("seriesLevel", seriesLevel);
        parameters.put("output", output);
        parameters.put("nonFeePaying", nonFeePaying);
        parameters.put("fromHid", fromHid);
        parameters.put("toHid", toHid);
        parameters.put("specialLogic", specialLogic);
        parameters.put("csvExportPath", csvExportPath);

        PortissUnFormattedReport<IncentiveFeeDetails> reportData = getUnformattedIncentiveFeeReport(fundId, statementDate, format, mode, seriesLevel, output, nonFeePaying, fromHid, toHid, specialLogic);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);

        return portissReport;
    }

    //----------------------------Fee Calculaton----------
    @PojoUsed(FeeCalculationRows.class)
    @RequestMapping(value = "/fee-calculation", method = RequestMethod.POST)
    public PortissFormattedReport getFeeCalculationRows(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("FORMAT") int format,
            @RequestParam("MODE") int mode,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("fundId", fundId);
        parameters.put("statementDate", statementDate);
        parameters.put("format", format);
        parameters.put("mode", mode);
        parameters.put("specialLogic", specialLogic);

        PortissUnFormattedReport<FeeCalculationRows> reportData = getUnformattedFeeCalculationRows(fundId, statementDate, format, mode, specialLogic);

        PortissFormattedReport portissReport = dataFormatter.format(reportStatementId, fundId, parameters, reportData, csvExporter, csvExportPath);

        return portissReport;

    }

    @RequestMapping(value = "/raw/fee-calculation", method = RequestMethod.POST)
    public PortissUnFormattedReport<FeeCalculationRows> getUnformattedFeeCalculationRows(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("STATEMENT_DATE") LocalDate statementDate,
            @RequestParam("FORMAT") int format,
            @RequestParam("MODE") int mode,
            @RequestParam("SPECIAL_LOGIC") String specialLogic) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        FundDto fundDto = fundService.getFundDto(fundKey);
        List<FeeCalculationRows> mainReportData = feeCalculationLocalService.getFeeCalculationRows(fundKey, statementDate, format, mode, specialLogic);
        PortissUnFormattedReport<FeeCalculationRows> unFormattedReport = new PortissUnFormattedReport<FeeCalculationRows>(getReportHeader(fundDto, fundKey, statementDate), mainReportData, Collections.<InvestorSummary> emptyList());
        return unFormattedReport;
    }

    //

    private PortissReportHeader getReportHeader(FundDto fundDto, EntityKey<Fund> fundKey, LocalDate statementDate) {
        EntityDto entityDto = new EntityDto(fundDto.getFundName(), 0, Arrays.asList(fundDto));

        String message = getNavMessage(fundKey, statementDate, fundDto.getClientId());

        PortissReportHeader reportHeader = new PortissReportHeader(entityDto, null, message, null, null, null);
        return reportHeader;
    }

    private String getNavMessage(EntityKey<Fund> fundKey, LocalDate navDate, int clientId) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, navDate, clientId, 0);
        return navMessage.isPresent() ? navMessage.get() : "";
    }

}
