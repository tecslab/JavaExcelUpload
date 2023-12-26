package com.example.excelProcessor.config;

public enum ApplicationUserPermission {
    MANZANA_READ("student:read"),
    MANZANA_WRITE("student:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
