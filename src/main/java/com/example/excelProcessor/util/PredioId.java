package com.example.excelProcessor.util;

import java.io.Serializable;
import java.util.Objects;

public class PredioId implements Serializable {

    private String pre_man_zona;
    private String pre_man_sector;
    private String pre_man_manzana;
    private String pre_predio;

    public PredioId() {
    }

    public PredioId(String pre_man_zona,
                    String pre_man_sector,
                    String pre_man_manzana,
                    String pre_predio) {
        this.pre_man_zona = pre_man_zona;
        this.pre_man_sector = pre_man_sector;
        this.pre_man_manzana = pre_man_manzana;
        this.pre_predio = pre_predio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredioId predioId = (PredioId) o;
        return Objects.equals(pre_man_zona, predioId.pre_man_zona) && Objects.equals(pre_man_sector, predioId.pre_man_sector) && Objects.equals(pre_man_manzana, predioId.pre_man_manzana) && Objects.equals(pre_predio, predioId.pre_predio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pre_man_zona, pre_man_sector, pre_man_manzana, pre_predio);
    }
}
