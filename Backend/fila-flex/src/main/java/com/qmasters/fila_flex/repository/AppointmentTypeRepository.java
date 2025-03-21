package com.qmasters.fila_flex.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;

@Repository
public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, Long> {
    //Busca AppointmentTypes que contenham a categoria especificada.
    @Query("SELECT a FROM AppointmentType a WHERE :category MEMBER OF a.category")List<AppointmentType> findByCategory(@Param("category") String category);

    //Busca AppointmentTypes que tenham o preço entre os valores especificados.
    @Query("SELECT a FROM AppointmentType a WHERE a.price BETWEEN :minPrice AND :maxPrice")
    List<AppointmentType> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    //Busca por nome.
    Optional<AppointmentType> findByName(String name);

    List<AppointmentType> findAllByOrderByEstimatedTimeAsc();

    //Busca próximo número disponível para um appointmentType.
    @Query("SELECT COALESCE(MAX(a.queueOrder), 0) + 1 FROM Appointment a WHERE a.appointmentType.id = :appointmentTypeId")
    Integer findNextQueueNumberForAppointmentType(@Param("appointmentTypeId") Long appointmentTypeId);

    //Busca maior ordem atual na fila.
    @Query("SELECT COALESCE(MAX(a.queueOrder), 0) FROM Appointment a WHERE a.appointmentType.id = :appointmentTypeId")
    Integer findMaxQueueOrderForAppointmentType(@Param("appointmentTypeId") Long appointmentTypeId);

    //Busca todos os appointments em ordem para um tipo específico (por id).
    @Query("SELECT a FROM Appointment a WHERE a.appointmentType.id = :appointmentTypeId ORDER BY a.queueOrder ASC")
    List<Appointment> findAllByAppointmentTypeIdOrderByQueueOrder(@Param("appointmentTypeId") Long appointmentTypeId);

    //Buscar appointments com ordem maior que um valor específico.
    @Query("SELECT a FROM Appointment a WHERE a.appointmentType.id = :appointmentTypeId AND a.queueOrder > :queueOrder ORDER BY a.queueOrder ASC")
    List<Appointment> findAllWithQueueOrderGreaterThan(
            @Param("appointmentTypeId") Long appointmentTypeId, 
            @Param("queueOrder") Integer queueOrder);

    //Busca por nome do appointmentType.
    @Query("SELECT a FROM Appointment a WHERE a.appointmentType.name = :appointmentTypeName ORDER BY a.queueOrder ASC")
    List<Appointment> findByAppointmentTypeNameOrderByQueueOrder(@Param("appointmentTypeName") String appointmentTypeName);
}