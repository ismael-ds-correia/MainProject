package com.qmasters.fila_flex.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.AppointmentType;

@Repository
public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, Long> {
    //Busca AppointmentTypes que contenham a categoria especificada.
    @Query("SELECT a FROM AppointmentType a WHERE :category MEMBER OF a.category")List<AppointmentType> findByCategory(@Param("category") String category);
}