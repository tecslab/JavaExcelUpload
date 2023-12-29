package com.example.excelProcessor.repo;

import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.model.Predio;
import com.example.excelProcessor.util.PredioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PredioRepo extends JpaRepository<Predio, PredioId> {
    Predio findByClaveCatastral(String claveCatastral);

    @Query("SELECT m FROM Predio m WHERE m.claveCatastral IN :clavesCatastrales")
    List<Predio> findBatchByClaveCatastral(@Param("clavesCatastrales") List<String> clavesCatastrales);
}
