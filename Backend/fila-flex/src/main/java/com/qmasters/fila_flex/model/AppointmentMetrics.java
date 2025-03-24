package com.qmasters.fila_flex.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AppointmentMetrics {

    // Lista para armazenar os agendamentos
    private List<Appointment> appointments;

    public AppointmentMetrics() {
        this.appointments = new ArrayList<>();
    }

    // Método para adicionar um agendamento à lista
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    // Método para calcular o tempo médio de espera da fila
    public double calculateAverageWaitTime() {
        long totalWaitTime = 0;
        int count = 0;

        for (Appointment appointment : appointments) {
            if (appointment.getScheduledDateTime() != null && appointment.getStartTime() != null) {
                totalWaitTime += Duration.between(appointment.getScheduledDateTime(), appointment.getStartTime()).toMinutes();
                count++;
            }
        }

        return count > 0 ? (double) totalWaitTime / count : 0;
    }

    // Método para calcular o tempo médio de atendimento
    public double calculateAverageServiceTime() {
        long totalServiceTime = 0;
        int count = 0;

        for (Appointment appointment : appointments) {
            if (appointment.getStartTime() != null && appointment.getCreatedDateTime() != null) {
                totalServiceTime += Duration.between(appointment.getStartTime(), appointment.getCreatedDateTime()).toMinutes();
                count++;
            }
        }

        return count > 0 ? (double) totalServiceTime / count : 0;
    }

    // Método para contar o número de atendimentos realizados
    public int countCompletedAppointments() {
        int count = 0;

        for (Appointment appointment : appointments) {
            if (appointment.getStartTime() != null && appointment.getCreatedDateTime() != null) {
                count++;
            }
        }

        return count;
    }
}
