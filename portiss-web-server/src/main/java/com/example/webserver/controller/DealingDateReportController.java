package com.example.webserver.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imsi.iss.portiss.formatter.service.DataFormatter;
import com.imsi.iss.portiss.rmi.api.dto.PortissUnFormattedReport;
import com.imsi.iss.portiss.rmi.api.dto.partnerstatement.dealingdate.DealingDateGenericReport;
import com.imsi.iss.portiss.rmi.api.meta.PojoUsed;
import com.imsi.iss.portiss.rmi.dto.EntityDto;
import com.imsi.iss.portiss.rmi.dto.InvestorSummary;
import com.imsi.iss.portiss.rmi.dto.PortissFormattedReport;
import com.imsi.iss.portiss.rmi.dto.PortissReportHeader;
import com.imsi.iss.portiss.server.entity.EntityKey;
import com.imsi.iss.portiss.server.entity.Fund;
import com.imsi.iss.portiss.server.service.local.FundService;
import com.imsi.iss.portiss.server.service.local.MonthEndNavService;
import com.imsi.iss.portiss.storedproc.service.DealingDateGenericLocalService;

@RestController
public class DealingDateReportController {

    private final DealingDateGenericLocalService dealingDateLocalService;
    private final MonthEndNavService monthEndNavService;
    private final DataFormatter dataFormatter;
    private final FundService fundService;

    @Autowired
    public DealingDateReportController(DealingDateGenericLocalService dealingDateLocalService, MonthEndNavService monthEndNavService, DataFormatter dataFormatter, FundService fundService) {
        this.dealingDateLocalService = dealingDateLocalService;
        this.monthEndNavService = monthEndNavService;
        this.dataFormatter = dataFormatter;
        this.fundService = fundService;
    }

    @RequestMapping(value = "/raw/dealing-date-report", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateReport(@RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("SHOW_NAV_ALL") int showNavAll,
            @RequestParam("SHOW_TRANS_TYPE") int showTransType,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("USE_CLASS_2") int useClass2,
            @RequestParam("IGNORE_PARTNERSHIPS") int ignorePartnerships,
            @RequestParam("MISC_SUB_CLIENT_GRP") String miscSubClientGrp,
            @RequestParam("APPROVAL_STATUS") int approvalStatus,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,

            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateGenericReportRows(new EntityKey<>(Fund.class, fundId), dateFrom, dateTo,
                showAllFuture, entityType, method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans, showSharesOnPartner, showNavAll, showTransType, reportLevel, specialLogic,
                useClass2, ignorePartnerships, miscSubClientGrp, approvalStatus, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, excludeFlaggedInv, portissLogic,
                csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());
    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("SHOW_NAV_ALL") int showNavAll,
            @RequestParam("SHOW_TRANS_TYPE") int showTransType,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("USE_CLASS_2") int useClass2,
            @RequestParam("IGNORE_PARTNERSHIPS") int ignorePartnerships,
            @RequestParam("MISC_SUB_CLIENT_GRP") String miscSubClientGrp,
            @RequestParam("APPROVAL_STATUS") int approvalStatus,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam("EXCLUDE_BASE_CUR") int excludeBaseCurrency) {
        if (excludeBaseCurrency == 1) {
            passParam += "_EXCL_BASE_CUR";
        }
        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateReport(fundId, dateFrom, dateTo,
                showAllFuture, entityType, method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans, showSharesOnPartner, showNavAll, showTransType, reportLevel, specialLogic,
                useClass2, ignorePartnerships, miscSubClientGrp, approvalStatus, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, excludeFlaggedInv, portissLogic,
                csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/dynamic", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateDynamicReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("MISC_COLUMNS") int miscColumns,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("USE_CLASS_2") int useClass2,
            @RequestParam("HOLDBACK_PERCENT") int holdbackPercent,
            @RequestParam("IGNORE_PARTNERSHIPS") int ignorePartnerships,
            @RequestParam("APPROVAL_STATUS") int approvalStatus,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("EXCLUDE_INVESTORS_DDR") int exclInvestors,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam("TEMPLATE_NAME") String templateName,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateGenericDynamicReportRows(new EntityKey<>(Fund.class, fundId), dateFrom, dateTo, showAllFuture, entityType,
                method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans, showSharesOnPartner, miscColumns, specialLogic, reportLevel, useClass2, holdbackPercent, ignorePartnerships,
                approvalStatus, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, exclInvestors, portissLogic, templateName, csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());
    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/dynamic", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateDynamicReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("MISC_COLUMNS") int miscColumns,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("USE_CLASS_2") int useClass2,
            @RequestParam("HOLDBACK_PERCENT") int holdbackPercent,
            @RequestParam("IGNORE_PARTNERSHIPS") int ignorePartnerships,
            @RequestParam("APPROVAL_STATUS") int approvalStatus,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("EXCLUDE_INVESTORS_DDR") int exclInvestors,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam("TEMPLATE_NAME") String templateName) {

        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateDynamicReport(fundId, dateFrom, dateTo, showAllFuture, entityType, method,
                nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans, showSharesOnPartner, miscColumns, specialLogic, reportLevel, useClass2, holdbackPercent, ignorePartnerships,
                approvalStatus, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, exclInvestors, portissLogic, templateName, csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/canyon", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateCanyonReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("UN_DESIGNATIONS") int undesignations,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateCanyonReportRows(new EntityKey<>(Fund.class, fundId),
                dateFrom, dateTo, showAllFuture, entityType, method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans,
                showSharesOnPartner, reportLevel, specialLogic, undesignations, showTransType1, showTransType2,
                showTransType3, showTransType4, showTransType5, portissLogic, csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());
    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/canyon", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateCanyonReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("UN_DESIGNATIONS") int undesignations,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic) {
        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateCanyonReport(fundId,
                dateFrom, dateTo, showAllFuture, entityType, method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans,
                showSharesOnPartner, reportLevel, specialLogic, undesignations, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, portissLogic, csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/settle", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingSettleDateReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_TRANS_TYPE") int showTransType,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("PORTISS_LOGIC") String portissLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingSettleDateReportRows(new EntityKey<>(Fund.class, fundId),
                dateFrom, dateTo, showAllFuture, entityType, method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans,
                showSharesOnPartner, specialLogic, showTransType, showTransType1, showTransType2,
                showTransType3, showTransType4, showTransType5, portissLogic, csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());
    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/settle", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingSettleDateReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParam,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_TRANS_TYPE") int showTransType,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam("PORTISS_LOGIC") String portissLogic) {

        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingSettleDateReport(fundId,
                dateFrom, dateTo, showAllFuture, entityType, method, nctInclude, lazard, lehman, sort, userId, passParam, showNoTrans,
                showSharesOnPartner, specialLogic, showTransType, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, portissLogic, csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/jpm", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateJpmReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("MISC_COLUMNS") int miscColumns,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("DEBUG_FLAG") int debugFlag,
            @RequestParam("EXPORT") int export,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateGenericJpmReportRows(new EntityKey<>(Fund.class, fundId), dateFrom, dateTo, showAllFuture, entityType,
                method, nctInclude, lazard, lehman, sort, userId, passParams, showNoTrans, showSharesOnPartner, miscColumns, specialLogic, reportLevel, debugFlag, export, showTransType1,
                showTransType2, showTransType3, showTransType4, showTransType5, portissLogic, csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());
    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/jpm", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateJpmReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("MISC_COLUMNS") int miscColumns,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("REPORT_LEVEL") int reportLevel,
            @RequestParam("DEBUG_FLAG") int debugFlag,
            @RequestParam("EXPORT") int export,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic) {
        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateJpmReport(fundId, dateFrom, dateTo, showAllFuture, entityType, method,
                nctInclude, lazard, lehman, sort, userId, passParams, showNoTrans, showSharesOnPartner, miscColumns, specialLogic, reportLevel, debugFlag, export, showTransType1, showTransType2,
                showTransType3, showTransType4, showTransType5, portissLogic, csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/convexity", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateConvexityReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("SHOW_NAV_ALL") int showNavAll,
            @RequestParam("SPLIT_RECORDS") int splitRecords,
            @RequestParam("EXCH_DATES") int exchDates,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam("TEMPLATE_NAME") String templateName,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateGenericConvexityReportRows(new EntityKey<>(Fund.class, fundId), dateFrom, dateTo, showAllFuture,
                entityType, method, nctInclude, lazard, lehman, sort, userId, passParams, showNoTrans, showSharesOnPartner, showNavAll, splitRecords, exchDates, showTransType1, showTransType2,
                showTransType3, showTransType4, showTransType5, specialLogic, portissLogic, templateName, csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());

    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/convexity", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateConvexityReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("SHOW_NAV_ALL") int showNavAll,
            @RequestParam("SPLIT_RECORDS") int splitRecords,
            @RequestParam("EXCH_DATES") int exchDates,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam("TEMPLATE_NAME") String templateName) {
        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateConvexityReport(fundId, dateFrom, dateTo, showAllFuture, entityType, method, nctInclude, lazard, lehman,
                sort, userId, passParams, showNoTrans, showSharesOnPartner, showNavAll, splitRecords, exchDates, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5,
                specialLogic, portissLogic, templateName, csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/harbinger", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateHarbingerReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("RED_SCHEDULE") int redSchedule,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {

        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateGenericHarbingerReportRows(new EntityKey<>(Fund.class, fundId), dateFrom, dateTo, showAllFuture,
                entityType, method, nctInclude, lazard, lehman, sort, userId, passParams, showNoTrans, showSharesOnPartner, redSchedule, specialLogic, showTransType1, showTransType2, showTransType3,
                showTransType4, showTransType5, portissLogic, csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());

    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/harbinger", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateHarbingerReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("METHOD") int method,
            @RequestParam("NCT_INCLUDE") int nctInclude,
            @RequestParam("LAZARD") int lazard,
            @RequestParam("LEHMAN") int lehman,
            @RequestParam("SORT") int sort,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_SHARES_ON_PARTNER") int showSharesOnPartner,
            @RequestParam("RED_SCHEDULE") int redSchedule,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic) {

        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateHarbingerReport(fundId, dateFrom, dateTo, showAllFuture, entityType, method, nctInclude, lazard, lehman,
                sort, userId, passParams, showNoTrans, showSharesOnPartner, redSchedule, specialLogic, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, portissLogic,
                csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    @RequestMapping(value = "/raw/dealing-date-report/lazard", method = RequestMethod.POST)
    public PortissUnFormattedReport<DealingDateGenericReport> getUnformattedDealingDateLazardReport(
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_TRANS_TYPE") int showTransType,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SORT") int sort,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic,
            @RequestParam(value = "CSV_EXPORT_PATH", required = false) String csvExportPath) {
        EntityKey<Fund> fundKey = new EntityKey<Fund>(Fund.class, fundId);
        EntityDto entityDto = fundService.getEntityDto(new EntityKey<Fund>(Fund.class, fundId), entityType);
        String message = getNavMessage(fundKey, dateFrom, entityDto.getFunds().get(0).getClientId(), entityType);

        DealingDateGenericReport dealingDateGenericReport = dealingDateLocalService.getDealingDateGenericLazardReportRows(new EntityKey<>(Fund.class, fundId), dateFrom, dateTo, showAllFuture, entityType,
                userId, passParams, showNoTrans, showTransType, specialLogic, sort, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, excludeFlaggedInv, portissLogic,
                csvExportPath);
        return new PortissUnFormattedReport<DealingDateGenericReport>(
                new PortissReportHeader(entityDto, null, message, null, null,
                        dealingDateLocalService.getCompanyName(new EntityKey<Fund>(Fund.class, fundId))),
                Arrays.asList(dealingDateGenericReport),
                Collections.<InvestorSummary> emptyList());

    }

    @PojoUsed(DealingDateGenericReport.class)
    @RequestMapping(value = "/dealing-date-report/lazard", method = RequestMethod.POST)
    public PortissFormattedReport getFormattedDealingDateLazardReport(
            @RequestParam("REPORT_RUN_ID") String reportRunId,
            @RequestParam("REPORT_STATEMENT_ID") long reportStatementId,
            @RequestParam("FUND_ID") long fundId,
            @RequestParam("DATE_FROM") LocalDate dateFrom,
            @RequestParam("DATE_TO") LocalDate dateTo,
            @RequestParam("SHOW_ALL_FUTURE") int showAllFuture,
            @RequestParam("ENTITY_TYPE") int entityType,
            @RequestParam("USERID") String userId,
            @RequestParam("PASSPARM") String passParams,
            @RequestParam("SHOW_NO_TRANS") int showNoTrans,
            @RequestParam("SHOW_TRANS_TYPE") int showTransType,
            @RequestParam("SPECIAL_LOGIC") String specialLogic,
            @RequestParam("SORT") int sort,
            @RequestParam("SHOW_TRANS_TYPE_1") int showTransType1,
            @RequestParam("SHOW_TRANS_TYPE_2") int showTransType2,
            @RequestParam("SHOW_TRANS_TYPE_3") int showTransType3,
            @RequestParam("SHOW_TRANS_TYPE_4") int showTransType4,
            @RequestParam("SHOW_TRANS_TYPE_5") int showTransType5,
            @RequestParam("EXCLUDE_FLAGGED_INV") int excludeFlaggedInv,
            @RequestParam("CSV_EXPORT_PATH") String csvExportPath,
            @RequestParam(value = "PORTISS_LOGIC", required = false) String portissLogic) {

        PortissUnFormattedReport<DealingDateGenericReport> reportData = getUnformattedDealingDateLazardReport(fundId, dateFrom, dateTo, showAllFuture, entityType, userId, passParams, showNoTrans,
                showTransType, specialLogic, sort, showTransType1, showTransType2, showTransType3, showTransType4, showTransType5, excludeFlaggedInv, portissLogic, csvExportPath);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dateFrom.compareTo(dateTo) == 0) {
            parameters.put("dateFrom", dateFrom);
            parameters.put("dateTo", " ");
        } else {
            parameters.put("dateFrom", dateFrom);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yy");
            String dateTO = dateTo.format(fmt);
            parameters.put("dateTo", " to " + dateTO);
        }
        return dataFormatter.format(reportStatementId, fundId, parameters, reportData, null, null);
    }

    private String getNavMessage(EntityKey<Fund> fundKey, LocalDate navDate, int clientId, int entityType) {
        Optional<String> navMessage = monthEndNavService.getEstimatedNavMessage(fundKey, navDate, clientId, entityType);
        return navMessage.isPresent() ? navMessage.get() : "";
    }

}