package com.qmasters.fila_flex.dto;

import java.time.LocalDateTime;

import com.qmasters.fila_flex.model.Appointment;

public class SimpleAppointmentDTO {
    private String appointmentTypeName;
    private String userEmail;
    private LocalDateTime scheduledDateTime;

    public SimpleAppointmentDTO() {
    }

    public SimpleAppointmentDTO(String appointmentTypeName, String userEmail, LocalDateTime scheduledDateTime) {
        this.appointmentTypeName = appointmentTypeName;
        this.userEmail = userEmail;
        this.scheduledDateTime = scheduledDateTime;
    }

    public SimpleAppointmentDTO toSimpleDTO(Appointment appointment) {
        return new SimpleAppointmentDTO(
            appointment.getAppointmentType().getName(),
            appointment.getUser().getEmail(), 
            appointment.getScheduledDateTime()
        );
    }

    public String getAppointmentTypeName() {
        return appointmentTypeName;
    }
    public void setAppointmentTypeName(String appointmentTypeName) {
        this.appointmentTypeName = appointmentTypeName;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    

}
