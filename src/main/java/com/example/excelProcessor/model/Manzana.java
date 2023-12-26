package com.example.excelProcessor.model;

//import jakarta.persistence.*; // for Spring Boot 3

import com.example.excelProcessor.util.ManzanaId;

import javax.persistence.*;

@Entity
//@Table(name = "manzanas")
@IdClass(ManzanaId.class)
@Table(name = "manzanas2024_aux")
public class Manzana {

    // It uses a composite key
    @Id
    private String man_zona;

    @Id
    private String man_sector;

    @Id
    private String man_manzana;

    @Column(name = "man_clave_manzana")
    private String claveManzana;

    @Column(name = "man_capital_incorporado")
    private Double capitalIncorporado;

    @Column(name = "man_tasa_renta")
    private Double tasaRenta;

    @Column(name = "man_tip_renta_id")
    private Integer tipoRenta;

    @Column(name = "man_valor_suelo")
    private Double valorSuelo;

    @Column(name = "man_usuario_ingreso")
    private String usuarioIngreso;

    public Manzana() {

    }

    public String getMan_zona() {
        return man_zona;
    }

    public void setMan_zona(String zona) {
        this.man_zona = zona;
    }

    public String getMan_sector() {
        return man_sector;
    }

    public void setMan_sector(String sector) {
        this.man_sector = sector;
    }

    public String getMan_manzana() {
        return man_manzana;
    }

    public void setMan_manzana(String manzana) {
        this.man_manzana = manzana;
    }

    public String getClaveManzana() {
        return claveManzana;
    }

    public void setClaveManzana(String claveManzana) {
        this.claveManzana = claveManzana;
    }

    public Double getCapitalIncorporado() {
        return capitalIncorporado;
    }

    public void setCapitalIncorporado(Double capitalIncorporado) {
        this.capitalIncorporado = capitalIncorporado;
    }

    public Double getTasaRenta() {
        return tasaRenta;
    }

    public void setTasaRenta(Double tasaRenta) {
        this.tasaRenta = tasaRenta;
    }

    public Integer getTipoRenta() {
        return tipoRenta;
    }

    public void setTipoRenta(Integer tipoRenta) {
        this.tipoRenta = tipoRenta;
    }

    public Double getValorSuelo() {
        return valorSuelo;
    }

    public void setValorSuelo(Double valorSuelo) {
        this.valorSuelo = valorSuelo;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    @Override
    public String toString() {
        return "Manzana{" +
                "zona='" + man_zona + '\'' +
                ", sector='" + man_sector + '\'' +
                ", manzana='" + man_manzana + '\'' +
                ", claveManzana='" + claveManzana + '\'' +
                ", capitalIncorporado=" + capitalIncorporado +
                ", tasaRenta=" + tasaRenta +
                '}';
    }
}
