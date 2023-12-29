package com.example.excelProcessor.model;

import com.example.excelProcessor.util.PredioId;

import javax.persistence.*;

@Entity
@IdClass(PredioId.class)
@Table(name = "predios")
public class Predio {

    @Id
    private String pre_predio;

    @Id
    private String pre_man_zona;

    @Id
    private String pre_man_sector;

    @Id
    private String pre_man_manzana;

    @Column(name = "pre_clave_catastral")
    private String claveCatastral;

    @Column(name = "pre_valor_unitario_base")
    private Double valorUnitarioBase;

    public Predio() {
    }

    public String getPre_predio() {
        return pre_predio;
    }

    public void setPre_predio(String pre_predio) {
        this.pre_predio = pre_predio;
    }

    public String getPre_man_zona() {
        return pre_man_zona;
    }

    public void setPre_man_zona(String pre_man_zona) {
        this.pre_man_zona = pre_man_zona;
    }

    public String getPre_man_sector() {
        return pre_man_sector;
    }

    public void setPre_man_sector(String pre_man_sector) {
        this.pre_man_sector = pre_man_sector;
    }

    public String getPre_man_manzana() {
        return pre_man_manzana;
    }

    public void setPre_man_manzana(String pre_man_manzana) {
        this.pre_man_manzana = pre_man_manzana;
    }

    public String getClaveCatastral() {
        return claveCatastral;
    }

    public void setClaveCatastral(String claveCatastral) {
        // Sometimes claveCatastral has 3 extra zeros at final
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
        return "Predio{" +
                "pre_predio='" + pre_predio + '\'' +
                ", pre_man_zona='" + pre_man_zona + '\'' +
                ", pre_man_sector='" + pre_man_sector + '\'' +
                ", pre_man_manzana='" + pre_man_manzana + '\'' +
                ", claveCatastral='" + claveCatastral + '\'' +
                ", valorUnitarioBase=" + valorUnitarioBase +
                '}';
    }
}
