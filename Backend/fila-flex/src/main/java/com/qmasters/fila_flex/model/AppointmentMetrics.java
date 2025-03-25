package com.qmasters.fila_flex.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentMetrics {

    private List<Appointment> appointments;

    public AppointmentMetrics(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    // Calcula o tempo médio de espera da fila
    public double calculateAverageWaitTime() {
        double totalWaitTime = 0;
        int count = 0;

        for (Appointment appointment : appointments) {
            LocalDateTime scheduledDateTime = appointment.getScheduledDateTime();
            LocalDateTime startTime = appointment.getStartTime();

            // Calcular o tempo de espera a partir do agendamento até o início do atendimento
            if (scheduledDateTime != null && startTime != null) {
                totalWaitTime += Duration.between(scheduledDateTime, startTime).toMinutes();
                count++;
            }
        }

        // Retorna a média de espera ou 0 se não houver agendamentos
        return count > 0 ? totalWaitTime / count : 0;
    }

    // Calcula o tempo médio de atendimento
    public double calculateAverageServiceTime() {
        double totalServiceTime = 0;
        int count = 0;

        for (Appointment appointment : appointments) {
            LocalDateTime startTime = appointment.getStartTime();
            LocalDateTime endTime = appointment.getEndTime();

            // Calcular o tempo de atendimento
            if (startTime != null && endTime != null) {
                totalServiceTime += Duration.between(startTime, endTime).toMinutes();
                count++;
            }
        }

        // Retorna a média de tempo de atendimento ou 0 se não houver agendamentos
        return count > 0 ? totalServiceTime / count : 0;
    }

    // Conta a quantidade de atendimentos realizados
    public int countCompletedAppointments() {
        int completedAppointments = 0;

        for (Appointment appointment : appointments) {
            if (appointment.getEndTime() != null) {
                completedAppointments++;
            }
        }

        return completedAppointments;
    }
}
