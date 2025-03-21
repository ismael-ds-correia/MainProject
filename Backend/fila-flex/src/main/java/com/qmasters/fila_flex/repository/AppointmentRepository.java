package com.qmasters.fila_flex.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qmasters.fila_flex.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    //função de retornar todos os agendamentos entre duas datas
    @Query("SELECT a FROM Appointment a WHERE a.scheduledDateTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByScheduledDateTime(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    //Função para buscar agendamentos por userId
    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId ORDER BY a.scheduledDateTime DESC")
    List<Appointment> findByUserId(@Param("userId") Long userId);

    //Função para buscar agendamentos num dado intervalo.
    @Query("SELECT a FROM Appointment a WHERE a.appointmentType.id = :appointmentTypeId AND a.queueOrder BETWEEN :startPosition AND :endPosition ORDER BY a.queueOrder ASC")
    List<Appointment> findAllWithPositionBetween(
            @Param("appointmentTypeId") Long appointmentTypeId,
            @Param("startPosition") Integer startPosition,
            @Param("endPosition") Integer endPosition);
}