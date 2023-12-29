package com.example.excelProcessor.util;

import java.util.List;

public class StatusObject {
    private List<String> rejectedKeys;
    private String statusDescription;

    public StatusObject() {
        this.statusDescription = "Processing";
    }

    public List<String> getRejectedKeys() {
        return rejectedKeys;
    }

    public void setRejectedKeys(List<String> rejectedKeys) {
        this.rejectedKeys = rejectedKeys;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
