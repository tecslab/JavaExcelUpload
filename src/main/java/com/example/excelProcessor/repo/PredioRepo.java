package com.example.excelProcessor.repo;

import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.util.PredioId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredioRepo extends JpaRepository<Predio, PredioId> {
    Predio findByClaveCatastral(String claveCatastral);
}
