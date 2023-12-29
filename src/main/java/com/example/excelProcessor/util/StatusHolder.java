package com.example.excelProcessor.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class StatusHolder {

    private final ConcurrentHashMap<Integer, StatusObject> statusMap = new ConcurrentHashMap<>();

    public StatusObject getStatus(Integer jobId) {
        return statusMap.getOrDefault(jobId, new StatusObject());
    }

    public void setStatus(Integer jobId, StatusObject status) {
        statusMap.put(jobId, status);
    }
}