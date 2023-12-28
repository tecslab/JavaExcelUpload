package com.example.excelProcessor.services;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.repo.ManzanaRepository;
import com.example.excelProcessor.repo.PredioRepo;
import com.example.excelProcessor.util.ManzanaRowData;
import com.example.excelProcessor.util.PredioRowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelUploadService {

    @Autowired
    private ManzanaRepository manzanaRepository;

    @Autowired
    CapIncorpService capIncorpService;

    @Autowired
    PredioRepo predioRepo;

    public List<String> updateManzanas(List<ManzanaRowData> rowDataList){
        List<String> rejectedKeys = new ArrayList<>();

        for (ManzanaRowData rowData : rowDataList) {
            String claveManzana = rowData.getClaveManzana();
            Manzana manzana = manzanaRepository.findByClaveManzana(claveManzana);

            if (manzana != null) {
                Double tasaRenta = rowData.getTasaRenta();
                Integer tipoRenta = rowData.getTipoRenta();
                Double valorSuelo = rowData.getValorSuelo();

                Double capIncorp = capIncorpService.calcularCapIncorp(claveManzana);

                manzana.setTasaRenta(tasaRenta);
                manzana.setTipoRenta(tipoRenta);
                manzana.setValorSuelo(valorSuelo);
                manzana.setCapitalIncorporado(capIncorp);

                manzanaRepository.save(manzana);
            } else {
                rejectedKeys.add(claveManzana);
                System.out.println("Registro no encontrado en la Base de Datos. Clave Manzana: " + claveManzana);
            }
        }
        return rejectedKeys;
    }

    public List<String> updatePredios(List<PredioRowData> rowDataList){
        List<String> rejectedKeys = new ArrayList<>();

        for (PredioRowData rowData : rowDataList) {
            String claveCatastral = rowData.getClaveCatastral();
            Predio predio = predioRepo.findByClaveCatastral(claveCatastral);

            if (predio != null) {
                Double valorUnitarioBase = rowData.getValorUnitarioBase();

                predio.setValorUnitarioBase(valorUnitarioBase);

                predioRepo.save(predio);
            } else {
                rejectedKeys.add(claveCatastral);
                System.out.println("Registro no encontrado en la Base de Datos. Clave Catastral: " + claveCatastral);
            }
        }
        return rejectedKeys;
    }
}
