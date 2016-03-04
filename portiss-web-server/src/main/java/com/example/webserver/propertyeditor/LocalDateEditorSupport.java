package com.example.webserver.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateEditorSupport extends PropertyEditorSupport {

    private final String dateFormat;

    public LocalDateEditorSupport(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void setAsText(String text) {
        setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern(dateFormat)));
    }

}
