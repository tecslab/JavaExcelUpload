package com.example.excelProcessor.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelPreProcess {

    // Column data contains column name and its position
    public static List<ColumnData> processHeaderRow(Row headerRow) {
        List<ColumnData> headers = new ArrayList<>();
        Iterator<Cell> cellIterator = headerRow.cellIterator();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            headers.add(new ColumnData(cell.getStringCellValue(), cell.getColumnIndex()));
        }
        return headers;
    }
}
