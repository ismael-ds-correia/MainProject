package com.qmasters.fila_flex.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.MetricsDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;

/**
 * Serviço responsável por gerar métricas relacionadas aos agendamentos.
 * 
 * Esta classe analisa os dados dos agendamentos para calcular estatísticas
 * importantes como tempo médio de espera e tempo médio de atendimento.
 * As métricas podem ser geradas para um tipo específico de agendamento,
 * com a possibilidade de filtrar por período de datas.
 * 
 * As métricas calculadas incluem:
 * - Total de agendamentos concluídos
 * - Tempo médio de espera entre check-in e início do atendimento (em minutos)
 * - Tempo médio de duração do atendimento (em minutos)
 */
@Service
public class AppointmentMetrics {
    private final AppointmentTypeRepository appointmentTypeRepository;

    public AppointmentMetrics(AppointmentRepository appointmentRepository, AppointmentTypeRepository appointmentTypeRepository) {
        this.appointmentTypeRepository = appointmentTypeRepository;
    }

    public MetricsDTO generateMetrics(String appointmentTypeName, LocalDateTime startDate, LocalDateTime endDate) {
        AppointmentType appointmentType = this.appointmentTypeRepository.findByName(appointmentTypeName)
                .orElseThrow(() -> new NoSuchElementException("Tipo de agendamento não encontrado."));

        List<Appointment> appointments = appointmentType.getAppointments();
        
        if (appointments.isEmpty()) {
            throw new NoSuchElementException("Não foram encontrados agendamentos para este tipo.");
        }
        
        //Filtrando por período de data.
        if (startDate != null && endDate != null) {
            appointments = new ArrayList<>(appointments.stream()
                .filter(a -> a.getScheduledDateTime() != null 
                        && !a.getScheduledDateTime().isBefore(startDate) 
                        && !a.getScheduledDateTime().isAfter(endDate))
                .toList());
                
            if (appointments.isEmpty()) {
                throw new NoSuchElementException("Não foram encontrados agendamentos no período especificado.");
            }
        }
        
        //Calculando métricas com todos os agendamentos disponíveis.
        Integer averageWaitingTime = calculateAverageWaitingTime(appointments);
        Integer averageServiceTime = calculateAverageServiceTime(appointments);
        
        // Criar e retornar o DTO
        return new MetricsDTO(
            appointments.size(),
            averageWaitingTime,
            averageServiceTime
        );
    }

    private Integer calculateAverageWaitingTime(List<Appointment> appointments) {
        var validAppointments = new ArrayList<>(appointments.stream()
            .filter(a -> a.getCheckInTime() != null && a.getStartTime() != null)
            .toList());
            
        if (validAppointments.isEmpty()) {
            return 0;
        }
        
        var totalWaitingTime = 0;
        for (var appointment : validAppointments) {
            totalWaitingTime += Duration.between(appointment.getCheckInTime(), appointment.getStartTime()).toMinutes();
        }
        return totalWaitingTime / validAppointments.size();
    }

    private Integer calculateAverageServiceTime(List<Appointment> appointments) {
        var validAppointments = new ArrayList<>(appointments.stream()
            .filter(a -> a.getStartTime() != null && a.getEndTime() != null)
            .toList());
            
        if (validAppointments.isEmpty()) {
            return 0;
        }
        
        var totalServiceTime = 0;
        for (var appointment : validAppointments) {
            totalServiceTime += Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
        }
        return totalServiceTime / validAppointments.size();
    }
}