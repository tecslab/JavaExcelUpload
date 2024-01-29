package com.example.excelProcessor.util;

public class PredioRowData {
    private String claveCatastral;
    private Double valorUnitarioBase;

    public String getClaveCatastral() {
        return claveCatastral;
    }

    public void setClaveCatastral(String claveCatastral) {
        String processedClave;
        if (claveCatastral.endsWith("000")) {
            processedClave = claveCatastral.substring(0, claveCatastral.length() - 3);
        } else {
            processedClave = claveCatastral;
        }
        this.claveCatastral = processedClave;
    }

    public Double getValorUnitarioBase() {
        return valorUnitarioBase;
    }

    public void setValorUnitarioBase(Double valorUnitarioBase) {
        this.valorUnitarioBase = valorUnitarioBase;
    }

    @Override
    public String toString() {
        return "PredioRowData{" +
                "claveCatastral='" + claveCatastral + '\'' +
                ", valorUnitarioBase='" + valorUnitarioBase + '\'' +
                '}';
    }
}
