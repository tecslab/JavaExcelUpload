package com.example.excelProcessor.util;

public class ManzanaRowData {
    private String claveManzana;
    private Double tasaRenta;
    private Double valorSuelo;
    private Integer tipoRenta;

    // Constructors, getters, and setters...

    public String getClaveManzana() {
        return claveManzana;
    }

    public void setClaveManzana(String claveManzana) {
        this.claveManzana = claveManzana;
    }

    public Double getTasaRenta() {
        return tasaRenta;
    }

    public void setTasaRenta(Double tasaRenta) {
        this.tasaRenta = tasaRenta;
    }

    public Double getValorSuelo() {
        return valorSuelo;
    }

    public void setValorSuelo(Double valorSuelo) {
        this.valorSuelo = valorSuelo;
    }

    public Integer getTipoRenta() {
        return tipoRenta;
    }

    public void setTipoRenta(Integer tipoRenta) {
        this.tipoRenta = tipoRenta;
    }

    @Override
    public String toString() {
        return "RowData{" +
                "claveManzana=" + claveManzana +
                ", tasaRenta=" + tasaRenta +
                ", valorSuelo=" + valorSuelo +
                ", tipoRenta=" + tipoRenta +
                '}';
    }
}
