package com.example.excelProcessor.services;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.repo.ManzanaRepository;
import com.example.excelProcessor.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ManzanasProcessingService {

   @Autowired
   StatusHolder statusHolder;

   @Autowired
   private ManzanaRepository manzanaRepository;

   @Autowired
   CapIncorpService capIncorpService;

   @Async
   public List<String> processManzanas(Sheet sheet, List<ColumnData> headers, Integer jobId){
      List<String> rejectedKeys  = new ArrayList<>();
      int batchSize = 1000;
      StatusObject status = new StatusObject();
      statusHolder.setStatus(jobId, status);

      try {
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
                  rejectedKeys.addAll(updateManzanas(batch));
                  batch.clear();
               }
            }
            rowIndex++;
         }
         // Process the remaining records in the last batch
         if (!batch.isEmpty()) {
            rejectedKeys.addAll(updateManzanas(batch));
         }
         System.out.println("Se procesó el archivo exitosamente");
         status.setStatusDescription("Complete");
         status.setRejectedKeys(rejectedKeys);
         statusHolder.setStatus(jobId, status);
      }catch (Error e) {
         e.printStackTrace();
      }
      return rejectedKeys;
   }

   @Transactional
   public List<String> updateManzanas(List<ManzanaRowData> rowDataList){ // receives a batch
      List<String> rejectedKeys = new ArrayList<>();
      List<String> clavesManzanas = new ArrayList<>();

      for (ManzanaRowData rowData : rowDataList){
         String claveManzana = rowData.getClaveManzana();
         clavesManzanas.add(claveManzana);
      }

      List<Manzana> manzanasToUpdate = manzanaRepository.findBatchByClaveManzana(clavesManzanas);
      // Entities are not retrieved in the same order that were searched

      for (Manzana manzana : manzanasToUpdate){
         ManzanaRowData rowData = findManzanaRowData(rowDataList, manzana.getClaveManzana());

         Double capIncorp = capIncorpService.calcularCapIncorp(rowData.getClaveManzana());

         // Comment these lines if you want to disable some dabase update
         manzana.setTasaRenta( rowData.getTasaRenta());
         manzana.setTipoRenta(rowData.getTipoRenta());
         manzana.setValorSuelo(rowData.getValorSuelo());
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
