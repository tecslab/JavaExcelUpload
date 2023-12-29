package com.example.excelProcessor.services;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.repo.ManzanaRepository;
import com.example.excelProcessor.repo.PredioRepo;
import com.example.excelProcessor.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelUploadService {

    @Autowired
    private ManzanaRepository manzanaRepository;

    @Autowired
    CapIncorpService capIncorpService;

    @Autowired
    PredioRepo predioRepo;

    @Transactional
    public List<String> updateManzanas(List<ManzanaRowData> rowDataList){
        List<String> rejectedKeys = new ArrayList<>();
        List<String> clavesManzanas = new ArrayList<>();

        for (ManzanaRowData rowData : rowDataList) {
            String claveManzana = rowData.getClaveManzana();
            clavesManzanas.add(claveManzana);
        }

        List<Manzana> manzanasToUpdate = manzanaRepository.findBatchByClaveManzana(clavesManzanas);   // It won't get a null entity
        // Entities are not retrieved in the same order that were searched

        for (Manzana manzana : manzanasToUpdate) {
            ManzanaRowData rowData = findManzanaRowData(rowDataList, manzana.getClaveManzana());

            Double tasaRenta = rowData.getTasaRenta();
            Integer tipoRenta = rowData.getTipoRenta();
            Double valorSuelo = rowData.getValorSuelo();

            Double capIncorp = capIncorpService.calcularCapIncorp(rowData.getClaveManzana());

            manzana.setTasaRenta(tasaRenta);
            manzana.setTipoRenta(tipoRenta);
            manzana.setValorSuelo(valorSuelo);
            manzana.setCapitalIncorporado(capIncorp);
        }

        for (ManzanaRowData rowData : rowDataList) {
            // reamaining claves are either wrong claves or duplicated, both are rejected
            String claveManzana = rowData.getClaveManzana();
            rejectedKeys.add(claveManzana);
        }
        manzanaRepository.saveAll(manzanasToUpdate);

        return rejectedKeys;
    }

    private ManzanaRowData findManzanaRowData(List<ManzanaRowData> rowDataList, String claveManzana){
        Optional<ManzanaRowData>  foundManzanaRow = rowDataList.stream()
            .filter(manzanaRowData -> manzanaRowData.getClaveManzana().equals(claveManzana))
            .findFirst();
        foundManzanaRow.ifPresent(rowDataList::remove);

        return foundManzanaRow.get();
    }

    /* ---------------------------------------------*/
    /* ---------------------------------------------*/
    /* ---------------------------------------------*/
    /* ---------------------------------------------*/

    @Async
    public List<String> asyncUpdatePredios(Sheet sheet, List<ColumnData> headers){
        List<String> rejectedKeys  = new ArrayList<>();
        int batchSize = 1000;

        try {
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
                        rejectedKeys.addAll(updatePredios(batch));
                        batch.clear();
                    }
                }
                rowIndex++;
            }
            // Process the remaining records in the last batch
            if (!batch.isEmpty()) {
                rejectedKeys.addAll(updatePredios(batch));
            }
        }catch (Error e) {
            e.printStackTrace();
        }
        return rejectedKeys;
    }

    @Transactional
    public List<String> updatePredios(List<PredioRowData> rowDataList){ // receives a batch
        List<String> rejectedKeys = new ArrayList<>();
        List<String> clavesCatastrales = new ArrayList<>();

        for (PredioRowData rowData : rowDataList){
            String claveCatastral = rowData.getClaveCatastral();
            clavesCatastrales.add(claveCatastral);
        }

        List<Predio> prediosToUpdate = predioRepo.findBatchByClaveCatastral(clavesCatastrales);
        // Entities are not retrieved in the same order that were searched

        for (Predio predio : prediosToUpdate){
            PredioRowData rowData = findPredioRowData(rowDataList, predio.getClaveCatastral());

            Double valorUnitarioBase = rowData.getValorUnitarioBase();

            predio.setValorUnitarioBase(valorUnitarioBase);
        }
        for (PredioRowData rowData : rowDataList) {
            // reamaining claves are either wrong claves or duplicated, both are rejected
            String claveCatastral = rowData.getClaveCatastral();
            rejectedKeys.add(claveCatastral);
        }
        predioRepo.saveAll(prediosToUpdate);

        return rejectedKeys;
    }

    private PredioRowData findPredioRowData(List<PredioRowData> rowDataList, String claveCatastral){
        // Get predio row Data and remove it from rowDataList
        Optional<PredioRowData> foundPredioRow = rowDataList.stream()
                .filter(predioRowData -> predioRowData.getClaveCatastral().equals(claveCatastral))
                .findFirst();
        foundPredioRow.ifPresent(rowDataList::remove);
        return foundPredioRow.get();
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
