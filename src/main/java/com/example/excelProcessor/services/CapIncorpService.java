package com.example.excelProcessor.services;

import com.example.excelProcessor.repo.CapIncorpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CapIncorpService {

    @Autowired
    private final CapIncorpRepository capIncorpRepository;

    public CapIncorpService(CapIncorpRepository capIncorpRepository) {
        this.capIncorpRepository = capIncorpRepository;
    }

    public Double calcularCapIncorp(String concatenada) {
        // concatenada is claveManzana
        return capIncorpRepository.callEmisionFunction(concatenada);
    }
}