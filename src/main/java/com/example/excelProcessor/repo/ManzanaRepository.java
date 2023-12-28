package com.example.excelProcessor.repo;
import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.util.ManzanaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// public interface ManzanaRepository extends JpaRepository<Manzana, Long> {
public interface ManzanaRepository extends JpaRepository<Manzana, ManzanaId> {
    Manzana findByClaveManzana(String claveManzana);

    @Query("SELECT m FROM Manzana m WHERE m.claveManzana IN :clavesManzanas")
    List<Manzana> findBatchByClaveManzana(@Param("clavesManzanas") List<String> clavesManzanas);
}