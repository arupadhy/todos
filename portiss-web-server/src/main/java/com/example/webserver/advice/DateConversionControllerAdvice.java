package com.example.webserver.advice;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

import com.example.webserver.propertyeditor.DateEditorSupport;
import com.example.webserver.propertyeditor.LocalDateEditorSupport;

@ControllerAdvice(annotations = RestController.class)
public class DateConversionControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditorSupport("yyyy-MM-dd"));
        binder.registerCustomEditor(Date.class, new DateEditorSupport("yyyy-MM-dd"));
    }

}
