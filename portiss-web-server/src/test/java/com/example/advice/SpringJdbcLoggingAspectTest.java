package com.example.advice;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.example.webserver.advice.SpringJdbcLoggingAspect;

import static org.junit.Assert.assertEquals;

public class SpringJdbcLoggingAspectTest {

    @Test
    public void testGetQueryWithParameters() {
        //given
        String expected = "EXEC p_part_stmt_gso @use_xref='0', @fund_id='17', @year_date='2006-02-28', "
                + "@prev_date='2006-02-28', @stmt_date='2006-03-31', @from_hid='1', @to_hid='1', "
                + "@fund_summary='0', @summary_funds=null, @investor_level ='0', @fund_to_date_info='0', @sort_order='3', "
                + "@signature_info='1', @first_name_first='1',@calc_incep_ror='0',@special_logic='UNGROUPED_ROR'";

        String sql = "EXEC p_part_stmt_gso @use_xref=:use_xref, @fund_id=:fund_id, @year_date=:year_date, "
                + "@prev_date=:prev_date, @stmt_date=:stmt_date, @from_hid=:from_hid, @to_hid=:to_hid, @fund_summary=:fund_summary, "
                + "@summary_funds=:summary_funds, @investor_level =:investorLevel, @fund_to_date_info=:fund_to_date_info, "
                + "@sort_order=:sort_order, @signature_info=:signature_info, @first_name_first=:first_name_first,"
                + "@calc_incep_ror=:calc_incep_ror,@special_logic=:special_logic";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("use_xref", 0);
        parameters.put("year_date", Date.valueOf("2006-02-28"));
        parameters.put("fund_id", 17L);
        parameters.put("prev_date", Date.valueOf("2006-02-28"));
        parameters.put("stmt_date", Date.valueOf("2006-03-31"));
        parameters.put("from_hid", 1);
        parameters.put("to_hid", 1);
        parameters.put("fund_summary", 0);
        parameters.put("summary_funds", null);
        parameters.put("fund_to_date_info", 0);
        parameters.put("sort_order", 3);
        parameters.put("signature_info", 1);
        parameters.put("first_name_first", 1);
        parameters.put("calc_incep_ror", 0);
        parameters.put("investorLevel", 0);
        parameters.put("special_logic", "UNGROUPED_ROR");

        SpringJdbcLoggingAspect testClass = new SpringJdbcLoggingAspect();

        //when
        String result = testClass.getQueryWithParameters(sql, parameters);

        //then
        assertEquals(expected, result);
    }

}
