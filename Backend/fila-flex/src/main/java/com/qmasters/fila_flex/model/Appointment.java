package com.qmasters.fila_flex.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_type_id", nullable = false)
    private AppointmentType appointmentType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    //mudei para "scheduled" para não ter muita repetição de "appointment"
    @Column(nullable = false)
    private LocalDateTime scheduledDateTime; //dia que ocorrerá o agendamento

    @Column(nullable = false)
    private LocalDateTime scheduledTime; //hora em que ocorrerá o agendamento

    @Column(nullable = false)
    private LocalDateTime createdDateTime; //util para determinar que não é mais possivel cancelar/reagendar o agendamento

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) //talvez adicionar um mappedBy
    private Adress adress; //endereço do local que ocorrerá o agendamento

    /*
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status; //pensando se precisa mesmo estar aqui
    */

    public Appointment() {
    
    }

    public Appointment(AppointmentType appointmentType, User user, LocalDateTime scheduledDateTime, LocalDateTime scheduledTime, LocalDateTime createdDateTime, Adress adress) {
        this.appointmentType = appointmentType;
        this.user = user;
        this.scheduledDateTime = scheduledDateTime;
        this.scheduledTime = scheduledTime;
        this.createdDateTime = createdDateTime;
        this.adress = adress;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
