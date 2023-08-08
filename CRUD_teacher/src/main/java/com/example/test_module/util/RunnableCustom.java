package com.example.test_module.util;

import java.util.Map;

public abstract class RunnableCustom implements Runnable {

    protected String fieldName;
    protected String value;
    protected String message;
    protected Map<String, String> errors;

    public void setValue(String value) {
        this.value = value;
    }
}
