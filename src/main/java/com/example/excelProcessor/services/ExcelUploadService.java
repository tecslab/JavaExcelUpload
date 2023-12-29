package com.example.excelProcessor.services;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.repo.ManzanaRepository;
import com.example.excelProcessor.repo.PredioRepo;
import com.example.excelProcessor.util.ManzanaRowData;
import com.example.excelProcessor.util.PredioRowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
}
