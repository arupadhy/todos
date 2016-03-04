package com.example.webserver.advice;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SpringJdbcLoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(SpringJdbcLoggingAspect.class);

    private static final Pattern PARAM_NAME_PATTERN = Pattern.compile(":\\w+");

    @SuppressWarnings("unchecked")
    @Before("execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations.*(String, java.util.Map, ..))")
    public void logSqlQuery(final JoinPoint joinPoint) {

        if (!LOG.isDebugEnabled()) {
            return;
        }

        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] arguments = joinPoint.getArgs();

        StringBuilder sb = new StringBuilder(500);
        sb.append("Executing: ").append(className).append(".").append(methodName).append("(), Query:\n ").append(getQueryWithParameters((String) arguments[0], (Map<String, Object>) arguments[1]));

        LOG.debug(sb.toString());
    }

    String getQueryWithParameters(String sql, Map<String, Object> parameters) {

        Matcher matcher = PARAM_NAME_PATTERN.matcher(sql);
        StringBuffer sqlWithParamValues = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group().substring(1);
            Object paramValue = parameters.get(paramName);
            String paramValueAsString;

            if (paramValue == null) {
                paramValueAsString = "null";
            } else {
                paramValueAsString = "'" + paramValue + "'";
            }

            matcher.appendReplacement(sqlWithParamValues, paramValueAsString);
        }

        matcher.appendTail(sqlWithParamValues);

        return sqlWithParamValues.toString();
    }

}
