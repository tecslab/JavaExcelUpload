package com.example.excelProcessor.repo;

import com.example.excelProcessor.model.CapIncorp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CapIncorpRepository extends JpaRepository<CapIncorp, Float> {

    @Query(value = "SELECT CST.EMISION2024.cal_impuesto_tr(NULL,NULL,NULL,NULL,NULL,?1,'M',2024) FROM DUAL", nativeQuery = true)
    Double callEmisionFunction(String claveManzana);

}
