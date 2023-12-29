package com.example.excelProcessor.util;

public class PredioRowData {
    private String claveCatastral;
    private Double valorUnitarioBase;

    public String getClaveCatastral() {
        return claveCatastral;
    }

    public void setClaveCatastral(String claveCatastral) {
        String processedClave = claveCatastral;
        if (processedClave.length()==13){
            processedClave = processedClave.substring(0, 10);
        }else if (processedClave.length()>13){
            processedClave = processedClave.substring(0, 12);
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
