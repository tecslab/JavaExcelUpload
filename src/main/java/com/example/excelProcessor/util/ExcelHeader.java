package com.example.excelProcessor.util;

public enum ExcelHeader {

    // Usados En Manzanas
    CLAVE_MANZANA("codmanzana"),
    TASA_RENTA("Tasa de Renta"),
    VALOR_SUELO("Precio Unitario Base (m2)"),
    TIPO_RENTA("Tipo de Renta"),

    // Usados En Predios
    CLAVE_CATASTRAL("clave"),
    VALOR_UNITARIO_BASE("PUB");



    private String nombreEnExcel;

    private ExcelHeader(String nombreEnExcel){
        this.nombreEnExcel = nombreEnExcel;
    }

    public String getNombreEnExcel() {
        return nombreEnExcel;
    }

    // Method to get the enum constant from a header name
    public static ExcelHeader fromHeaderName(String headerName) {
        for (ExcelHeader header : values()) {
            if (header.getNombreEnExcel().equalsIgnoreCase(headerName)) {
                return header;
            }
        }
        // throw new IllegalArgumentException("No enum constant with headerName: " + headerName);
        return null;
    }
}