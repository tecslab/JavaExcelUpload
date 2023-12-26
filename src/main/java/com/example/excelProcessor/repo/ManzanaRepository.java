package com.example.excelProcessor.repo;
import com.example.excelProcessor.model.Manzana;
import com.example.excelProcessor.util.ManzanaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// public interface ManzanaRepository extends JpaRepository<Manzana, Long> {
public interface ManzanaRepository extends JpaRepository<Manzana, ManzanaId> {
    List<Manzana> findByUsuarioIngreso(String usuarioIngreso);
    Manzana findByClaveManzana(String claveManzana);
}