package com.example.excelProcessor.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CapIncorp {

    @Id
    private Double result;

    public CapIncorp() {

    }

    public CapIncorp(Double result) {
        this.result = result;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }
}
