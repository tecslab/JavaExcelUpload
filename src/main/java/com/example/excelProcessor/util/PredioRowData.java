package com.example.excelProcessor.util;

public class PredioRowData {
    private String claveCatastral;
    private Double valorUnitarioBase;

    public String getClaveCatastral() {
        return claveCatastral;
    }

    public void setClaveCatastral(String claveCatastral) {
        this.claveCatastral = claveCatastral;
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
