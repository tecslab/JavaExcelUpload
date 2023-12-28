package com.example.excelProcessor.controller;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.repo.ManzanaRepository;
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

//@CrossOrigin(origins = "http://localhost:8081")
//@CrossOrigin(origins = "null")
@CrossOrigin(origins = "http://localhost:5173")
@Controller
@RequestMapping("/api")
public class ManzanaController {

    @Autowired
    ManzanaRepository manzanaRepository;

    @Autowired
    ExcelUploadService excelUploadService;

    //@GetMapping("/manzanas/{id}")
    @GetMapping("/manzanas/{zona}/{sector}/{manzana}")
    public ResponseEntity<Manzana> getManzanaById(@PathVariable String zona,
                                                  @PathVariable String sector,
                                                  @PathVariable String manzana) {
        System.out.println("Recuperando manzana....");
        ManzanaId compositeKey = new ManzanaId(zona, sector, manzana);

        Optional<Manzana> manzanaData = manzanaRepository.findById(compositeKey);

        if (manzanaData.isPresent()) {
            return new ResponseEntity<>(manzanaData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload")
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
                List<ManzanaRowData> batch = new ArrayList<>();
                while (rowIndex <= sheet.getLastRowNum()) {
                    if (sheet.getRow(rowIndex) != null) { // to skip rows with no data
                        ManzanaRowData rowData = processDataRow(sheet.getRow(rowIndex), headers);
                        if (rowData.getClaveManzana() != null && !rowData.getClaveManzana().isEmpty()) {
                            // to skip rows without a key or empty rows that were deleted by the user in excel
                            batch.add(rowData);
                        } else {
                            System.out.println("Valor sin clave de manzana en la fila " + rowIndex);
                        }
                        // Process the batch when it reaches the specified size
                        if (batch.size() >= batchSize) {
                            rejectedKeys.addAll(excelUploadService.updateManzanas(batch));
                            batch.clear();
                        }
                    }
                    rowIndex++;
                }

                // Process the remaining records in the last batch
                if (!batch.isEmpty()) {
                    rejectedKeys.addAll(excelUploadService.updateManzanas(batch));
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
        boolean tieneClaveManzana = false, tieneTasaRenta = false, tieneValorSuelo= false, tieneTipoRenta= false;
        for (int i = 0; i < headers.size(); i++) {
            String headerName = headers.get(i).getName();
            ExcelHeader header = ExcelHeader.fromHeaderName(headerName); // instance a Header if there exists
            if (header != null) {
                switch (header) {
                    case CLAVE_MANZANA:
                        tieneClaveManzana = true;
                        break;
                    case TASA_RENTA:
                        tieneTasaRenta = true;
                        break;
                    case VALOR_SUELO:
                        tieneValorSuelo = true;
                        break;
                    case TIPO_RENTA:
                        tieneTipoRenta = true;
                        break;
                }
            }
        }
        return tieneClaveManzana & tieneTasaRenta & tieneValorSuelo & tieneTipoRenta;
    }

    private ManzanaRowData processDataRow(Row dataRow, List<ColumnData> headers) {
        //Get data from excel cells and store it in RowData
        ManzanaRowData rowData = new ManzanaRowData();
        Iterator<Cell> cellIterator = dataRow.cellIterator();

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
                    case CLAVE_MANZANA:
                        if (cell.getCellType().equals(CellType.NUMERIC)){
                            rowData.setClaveManzana( String.valueOf(cell.getNumericCellValue()) );
                        }else{
                            rowData.setClaveManzana( cell.getStringCellValue());
                        }
                        break;
                    case TASA_RENTA:
                        rowData.setTasaRenta((double) cell.getNumericCellValue());
                        break;
                    case VALOR_SUELO:
                        rowData.setValorSuelo((double) cell.getNumericCellValue());
                        break;
                    case TIPO_RENTA:
                        rowData.setTipoRenta((int) cell.getNumericCellValue());
                        break;
                }
            }
        }
        return rowData;
    }
}
