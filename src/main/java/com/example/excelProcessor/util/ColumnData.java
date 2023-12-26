package com.example.excelProcessor.util;

import java.util.Objects;

public class ColumnData {
    private String name;
    private int place;

    public ColumnData(String name, int place) {
        this.name = name;
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "ColumnData{" +
                "name='" + name + '\'' +
                ", place=" + place +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnData that = (ColumnData) o;
        return place == that.place && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, place);
    }
}
