package com.example.excelProcessor.util;

import java.io.Serializable;
import java.util.Objects;

public class ManzanaId implements Serializable {

    private String man_zona;
    private String man_sector;
    private String man_manzana;

    public ManzanaId() {
    }

    public ManzanaId(String zona, String sector, String manzana) {
        this.man_zona = zona;
        this.man_sector = sector;
        this.man_manzana = manzana;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManzanaId that = (ManzanaId) o;
        return Objects.equals(man_zona, that.man_zona) && Objects.equals(man_sector, that.man_sector) && Objects.equals(man_manzana, that.man_manzana);
    }

    @Override
    public int hashCode() {
        return Objects.hash(man_zona, man_sector, man_manzana);
    }
}
