package com.example.excelProcessor.services;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.repo.ManzanaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExcelUploadService {

    @Autowired
    private ManzanaRepository manzanaRepository;

    @Autowired
    CapIncorpService capIncorpService;

    public List<String> updateDatabase(String claveManzana, Double tasaRenta, Integer tipoRenta, Double valorSuelo, List<String> rejectedKeys){

        //Optional<Manzana> manzanaData = manzanaRepository.findByClaveManzana(claveManzana);
        Manzana manzana = manzanaRepository.findByClaveManzana(claveManzana);

        if (manzana != null ) {
            Double capIncorp = capIncorpService.calcularCapIncorp(claveManzana);
            manzana.setTasaRenta(tasaRenta);
            manzana.setTipoRenta(tipoRenta);
            manzana.setValorSuelo(valorSuelo);
            manzana.setCapitalIncorporado(capIncorp);
            manzanaRepository.save(manzana);
        }else{
            rejectedKeys.add(claveManzana);
            System.out.println("Registro no encontrado en la Base de Datos. Clave Manzana: " + claveManzana);
        }
        return rejectedKeys;
    }
}
