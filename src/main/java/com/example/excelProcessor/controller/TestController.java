package com.example.excelProcessor.controller;

import com.example.excelProcessor.util.ColumnData;
import com.example.excelProcessor.util.ExcelPreProcess;
import com.example.excelProcessor.util.ManzanaRowData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class TestController {

   @PostMapping("/test/upload")
   public ResponseEntity<Integer> handleFileUpload(@RequestParam("file") MultipartFile file) {

      try {
         System.out.println("Se subió un archivo de -----");

         Workbook workbook = WorkbookFactory.create(file.getInputStream());
         Sheet sheet = workbook.getSheetAt(0);

         try {
            // Get batches of rowdata
            int rowIndex = 1; // Skip the header row
            List<ManzanaRowData> batch = new ArrayList<>();

         }catch (Error e) {
            e.printStackTrace();
         }

         return ResponseEntity.ok(5);
         //} catch (IOException | InvalidFormatException e) {
      } catch (IOException e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
      }
   }
}
