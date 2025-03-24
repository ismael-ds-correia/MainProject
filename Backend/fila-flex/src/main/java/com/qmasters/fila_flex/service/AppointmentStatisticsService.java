package com.qmasters.fila_flex.service;

import java.time.Duration;
import java.util.List;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.model.Appointment;

public class AppointmentStatisticsService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentStatisticsService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Retorna o tempo médio de espera antes do atendimento
    public double getAverageWaitTime() {
        List<Appointment> appointments = appointmentRepository.findAll();
        if (appointments.isEmpty()) return 0;

        long totalWaitTime = appointments.stream()
            .filter(a -> a.getCheckInTime() != null && a.getStartTime() != null)
            .mapToLong(a -> Duration.between(a.getCheckInTime(), a.getStartTime()).toMinutes())
            .sum();

        return (double) totalWaitTime / appointments.size();
    }

    // Retorna o tempo médio de atendimento
    public double getAverageServiceTime() {
        List<Appointment> appointments = appointmentRepository.findAll();
        if (appointments.isEmpty()) return 0;

        long totalServiceTime = appointments.stream()
            .filter(a -> a.getStartTime() != null && a.getEndTime() != null)
            .mapToLong(a -> Duration.between(a.getStartTime(), a.getEndTime()).toMinutes())
            .sum();

        return (double) totalServiceTime / appointments.size();
    }

    // Retorna a quantidade total de atendimentos realizados
    public long getTotalAppointments() {
        return appointmentRepository.count();
    }
}