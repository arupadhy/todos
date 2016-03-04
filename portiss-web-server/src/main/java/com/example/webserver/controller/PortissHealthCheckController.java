package com.example.webserver.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imsi.iss.portiss.server.service.local.PortissHealthCheckService;

@Controller
public class PortissHealthCheckController {

    @Autowired
    private PortissHealthCheckService healthService;

    private Date serverStartTime;

    @PostConstruct
    public void init() {
        serverStartTime = new Date();
    }

    @RequestMapping(value = {"/", "/health", "/test"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String homePage() {
        return "health";
    }

    @RequestMapping(value = {"/loadHealth"}, produces = "application/json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody Object loadPortissStats() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", healthService.loadPotissStats());
        result.put("startTime", serverStartTime.toString());
        return result;
    }

    @ExceptionHandler(value = Throwable.class)
    public Object loadErrorPage() {
        return null;
    }

}
