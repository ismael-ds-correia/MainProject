package com.qmasters.fila_flex.service;

import java.time.LocalDateTime;
import java.util.List;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.model.Appointment;

import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final AppointmentRepository appointmentRepository;

    public ReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Função que retorna os agendamentos dentro do período fornecido
    public List<Appointment> getAppointmentsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByScheduledDateTime(startDate, endDate);
    }
}
