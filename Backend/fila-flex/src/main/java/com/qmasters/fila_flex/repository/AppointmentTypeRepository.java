package com.qmasters.fila_flex.repository;


import com.qmasters.fila_flex.model.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, Long> {
}