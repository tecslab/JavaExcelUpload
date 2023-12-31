package com.example.excelProcessor.controller;

import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.repo.PredioRepo;
import com.example.excelProcessor.services.PredioProcessingService;
import com.example.excelProcessor.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class PredioController {

    private Integer processIdCounter = 500; // begin in 500 to avoid overlap with manzanas

    private final PredioRepo predioRepository;
    private final PredioProcessingService predioProcessingService;

    @Autowired
    public PredioController(PredioRepo predioRepository, PredioProcessingService predioProcessingService) {
        this.predioRepository = predioRepository;
        this.predioProcessingService = predioProcessingService;
    }

    @GetMapping("/predios/{zona}/{sector}/{manzana}/{predio}")
    public ResponseEntity<Predio> getPredioById(@PathVariable String zona,
                                                @PathVariable String sector,
                                                @PathVariable String manzana,
                                                @PathVariable String predio){
        System.out.println("Recuperando Predio");

        PredioId predioCompositeKey = new PredioId(zona, sector, manzana, predio);

        Optional<Predio> predioEnt = predioRepository.findById(predioCompositeKey);

        if (predioEnt.isPresent()){
            return new ResponseEntity<>(predioEnt.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/predios/upload")
    public ResponseEntity<Integer> handleFileUpload(@RequestParam("file") MultipartFile file) {
        Integer jobId = processIdCounter;
        processIdCounter++;

        try {
            System.out.println("Se subió un archivo de Predios");

            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                List<ColumnData> headers = ExcelPreProcess.processHeaderRow(sheet.getRow(0));
                if (!validateFormat(headers)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
                }
                predioProcessingService.asyncUpdatePredios(sheet, headers, jobId);
                System.out.println("Se inició proceso asyncrono");
                return ResponseEntity.ok(jobId);
                // run asyc process
            }
        //} catch (IOException | InvalidFormatException e) {
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
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
}
