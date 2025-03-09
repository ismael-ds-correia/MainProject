package com.qmasters.fila_flex.dto;
import java.time.LocalDate;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.util.UserRole;

public class ScheduleDTO {

    //atributos
    private Appointment appointment;
    private User user;
    private LocalDate scheduledDate;
    

    //constructor
    public ScheduleDTO() {
    }

    public ScheduleDTO(Appointment appointment, User user, LocalDate scheduledDate) {
        this.appointment = appointment;
        this.user = user;
        this.scheduledDate = scheduledDate;

    }

    //getters e setters
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
}