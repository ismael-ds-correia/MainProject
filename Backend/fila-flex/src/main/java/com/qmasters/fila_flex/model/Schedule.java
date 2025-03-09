package com.qmasters.fila_flex.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "schedule")
public class Schedule {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    @JsonIgnore //usado para evitar loop infinito na saida do Insomnia
    private Appointment appointment;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore //usado para evitar loop infinito na saida do Insomnia
    private User user;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    //Construtores
    public Schedule() {}
    
    public Schedule(Appointment appointment, User user, LocalDate scheduledDate) {
        this.appointment = appointment;
        this.user = user;
        this.scheduledDate = scheduledDate;
    }

    //Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointment() {
        return appointment.getId();
    }
    
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Long getUser() {
        return user.getId();
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
