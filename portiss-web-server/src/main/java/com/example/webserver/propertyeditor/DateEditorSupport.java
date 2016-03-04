package com.example.webserver.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateEditorSupport extends PropertyEditorSupport {

    private final String dateFormat;

    public DateEditorSupport(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void setAsText(String text) {
        try {
            setValue(new SimpleDateFormat(dateFormat).parse(text));
        } catch (ParseException e) {
            throw new RuntimeException("could not parse the date: " + text + " with format: " + dateFormat, e);
        }
    }

}
