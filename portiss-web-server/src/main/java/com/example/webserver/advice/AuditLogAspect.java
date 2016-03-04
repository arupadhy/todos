package com.example.webserver.advice;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imsi.iss.portiss.server.service.local.AuditLogService;

@Component
@Aspect
public class AuditLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogAspect.class);

    private static final String JASPER_REPORT_RUN_KEY = "JASPER_REPORT_RUN_KEY";

    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object loggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        Date startTime = new Date(System.currentTimeMillis());
        long startTimeInMilliseconds = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();

        String jasperReportRunKey = null;

        if ((args.length > 2) && (args[0] instanceof String)) {
            jasperReportRunKey = (String) args[0];

            MDC.put(JASPER_REPORT_RUN_KEY, jasperReportRunKey);
        }

        Object output = joinPoint.proceed();

        if ((jasperReportRunKey == null) || !(args[1] instanceof Number)) {
            LOGGER.info("returning as the 1st argument is not String or the 2nd argument is not Number");
            return output;
        }

        long portissRunDuration = System.currentTimeMillis() - startTimeInMilliseconds;
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        StringBuilder serviceDetails = new StringBuilder(100);
        serviceDetails.append("Class: ").append(className).append(", Method: ").append(methodName);
        Number reportStatementId = (Number) args[1];

        auditLogService.updatePortissAuditLog(jasperReportRunKey, startTime, portissRunDuration, reportStatementId.longValue(), serviceDetails.toString());

        LOGGER.info("updated the log table");

        MDC.remove(JASPER_REPORT_RUN_KEY);

        return output;
    }

}
