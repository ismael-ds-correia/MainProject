package com.qmasters.fila_flex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.AppointmentTypeDetails;

@Repository
public interface AppointmentTypeDetailsRepository extends JpaRepository<AppointmentTypeDetails, Long> {
    
}
