package com.example.advice;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Test;
import org.mockito.Mockito;

import com.example.webserver.advice.AuditLogAspect;
import com.imsi.iss.portiss.server.service.local.AuditLogService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditLogAspectTest {
    @Test
    public void testLoggingAdvice() throws Throwable {
        //given
        Object dummyExpected = new Object();
        Date toDate = new Date();
        Date fromDate = new Date();
        Date statementDate = new Date();
        Object[] args = {"run_004", new Long(10), statementDate, toDate, fromDate, "400"};
        AuditLogService mockAuditLogService = mock(AuditLogService.class);
        ProceedingJoinPoint mockJoinPoint = mock(ProceedingJoinPoint.class);
        Signature mockSignature = mock(Signature.class);
        when(mockJoinPoint.proceed()).thenReturn(dummyExpected);
        when(mockJoinPoint.getSignature()).thenReturn(mockSignature);
        when(mockSignature.getDeclaringTypeName()).thenReturn("PartnerStatmentService");
        when(mockSignature.getName()).thenReturn("getPartnerStatment");
        when(mockJoinPoint.getArgs()).thenReturn(args);
        AuditLogAspect testClass = new AuditLogAspect(mockAuditLogService);
        //when
        Object result = testClass.loggingAdvice(mockJoinPoint);
        //then
        assertEquals(dummyExpected, result);
        verify(mockAuditLogService).updatePortissAuditLog(eq("run_004"), any(Date.class), Mockito.anyLong(), eq(new Long(10)), eq("Class: PartnerStatmentService, Method: getPartnerStatment"));

    }
}
