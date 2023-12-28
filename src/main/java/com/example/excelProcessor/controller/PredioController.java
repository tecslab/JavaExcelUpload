package com.example.excelProcessor.controller;

import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.repo.PredioRepo;
import com.example.excelProcessor.services.ExcelUploadService;
import com.example.excelProcessor.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class PredioController {

    @Autowired
    PredioRepo predioRepository;

    @Autowired
    ExcelUploadService excelUploadService;

    @GetMapping("/predios/{zona}/{sector}/{manzana}/{predio}")
    public ResponseEntity<Predio> getPredioById(@PathVariable String zona,
                                                @PathVariable String sector,
                                                @PathVariable String manzana,
                                                @PathVariable String predio){
        System.out.println("Reperando Predio");

        PredioId predioCompositeKey = new PredioId(zona, sector, manzana, predio);

        Optional<Predio> predioEnt = predioRepository.findById(predioCompositeKey);

        if (predioEnt.isPresent()){
            return new ResponseEntity<>(predioEnt.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/predios/upload")
    public ResponseEntity<List<String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        List<String> rejectedKeys  = new ArrayList<>();
        int batchSize = 1000;

        try {
            System.out.println("Se subió un archivo");

            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                // Process header row and extract column names and positions
                List<ColumnData> headers = ExcelPreProcess.processHeaderRow(sheet.getRow(0));
                if (!validateFormat(headers)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rejectedKeys);
                }

                // Get batches of rowdata
                int rowIndex = 1; // Skip the header row
                List<PredioRowData> batch = new ArrayList<>();
                while (rowIndex <= sheet.getLastRowNum()) {
                    if (sheet.getRow(rowIndex) != null) { // to skip rows with no data
                        PredioRowData rowData = processDataRow(sheet.getRow(rowIndex), headers);
                        if (rowData.getClaveCatastral() != null && !rowData.getClaveCatastral().isEmpty()) {
                            // to skip rows without a key or empty rows that were deleted by the user in excel
                            batch.add(rowData);
                        } else {
                            System.out.println("Valor sin clave de catastral en la fila " + rowIndex);
                        }
                        // Process the batch when it reaches the specified size
                        if (batch.size() >= batchSize) {
                            rejectedKeys.addAll(excelUploadService.updatePredios(batch));
                            batch.clear();
                        }
                    }
                    rowIndex++;
                }

                // Process the remaining records in the last batch
                if (!batch.isEmpty()) {
                    rejectedKeys.addAll(excelUploadService.updatePredios(batch));
                }
            }
            System.out.println("Archivo procesado correctamente");
            return ResponseEntity.ok(rejectedKeys);
            //} catch (IOException | InvalidFormatException e) {
        } catch (IOException e) {
            e.printStackTrace();
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rejectedKeys);
        }
    }

    private boolean validateFormat(List<ColumnData> headers){
        boolean tieneClaveCatastral = false, tieneValorUnitarioBase = false;
        for (int i = 0; i < headers.size(); i++) {
            String headerName = headers.get(i).getName();
            ExcelHeader header = ExcelHeader.fromHeaderName(headerName); // instance a Header if there exists
            if (header != null) {
                switch (header) {
                    case CLAVE_CATASTRAL:
                        tieneClaveCatastral = true;
                        break;
                    case VALOR_UNITARIO_BASE:
                        tieneValorUnitarioBase = true;
                        break;
                }
            }
        }
        return tieneClaveCatastral & tieneValorUnitarioBase;
    }

    private PredioRowData processDataRow(Row row, List<ColumnData> headers) {
        //Get data from excel cells and store it in RowData
        PredioRowData rowData = new PredioRowData();
        Iterator<Cell> cellIterator = row.cellIterator();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();
            String headerName = headers.stream()
                .filter(header -> header.getPlace() == columnIndex)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Column not found"))
                .getName();

            ExcelHeader header = ExcelHeader.fromHeaderName(headerName); // instance a Header if there exists

            if (header!= null){
                switch (header) {
                    case CLAVE_CATASTRAL:
                        if (cell.getCellType().equals(CellType.NUMERIC)){
                            rowData.setClaveCatastral( String.valueOf(cell.getNumericCellValue()) );
                        }else{
                            rowData.setClaveCatastral( cell.getStringCellValue());
                        }
                        break;
                    case VALOR_UNITARIO_BASE:
                        rowData.setValorUnitarioBase((double) cell.getNumericCellValue());
                        break;
                }
            }
        }
        return rowData;
    }

}
