package com.example.excelProcessor.controller;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.repo.ManzanaRepository;
import com.example.excelProcessor.services.ManzanasProcessingService;
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

//@CrossOrigin(origins = "http://localhost:8081")
//@CrossOrigin(origins = "null")
@CrossOrigin(origins = "http://localhost:5173")
@Controller
@RequestMapping("/api")
public class ManzanaController {

    private Integer processIdCounter = 1;

    @Autowired
    ManzanaRepository manzanaRepository;

    @Autowired
    ManzanasProcessingService manzanasProcessingService;

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
    public ResponseEntity<Integer> handleFileUpload(@RequestParam("file") MultipartFile file) {
        Integer jobId = processIdCounter;
        processIdCounter++;

        try {
            System.out.println("Se subió un archivo de Manzanas");

            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                List<ColumnData> headers = ExcelPreProcess.processHeaderRow(sheet.getRow(0));
                if (!validateFormat(headers)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
                }
                manzanasProcessingService.processManzanas(sheet, headers, jobId);
                System.out.println("Se inició proceso asyncrono");
                return ResponseEntity.ok(jobId);
            }
            //} catch (IOException | InvalidFormatException e) {
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
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


}
