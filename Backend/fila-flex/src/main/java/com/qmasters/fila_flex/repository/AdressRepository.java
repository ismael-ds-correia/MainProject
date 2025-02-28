package com.qmasters.fila_flex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.Adress;

@Repository
public interface AdressRepository extends JpaRepository<Adress, Long> {
    
}
