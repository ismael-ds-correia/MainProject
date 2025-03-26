package com.qmasters.fila_flex.dto.response_dto;

import java.util.List;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.User;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String role;
    private List<Appointment> appointments;

    public UserResponseDTO() {
    }

    public UserResponseDTO(User user, List<Appointment> appointments) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole().toString();
        this.appointments = appointments;

    }

    public Long getId() {
        return id;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
}
