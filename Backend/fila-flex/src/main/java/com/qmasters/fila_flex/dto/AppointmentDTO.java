package com.qmasters.fila_flex.dto;

import java.time.LocalDateTime;

import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.User;

public class AppointmentDTO {
    private AppointmentType appointmentType;
    private User user;
    private LocalDateTime scheduledDateTime;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdDateTime;
    private Adress adress;

    public AppointmentDTO() {
    }

    public AppointmentDTO(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime, LocalDateTime scheduledTime, LocalDateTime createdDateTime, Adress adress) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.scheduledTime = scheduledTime;
        this.createdDateTime = createdDateTime;
        this.adress = adress;
    }

    //getters e setters

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }
}