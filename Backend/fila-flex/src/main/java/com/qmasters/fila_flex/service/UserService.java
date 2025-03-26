package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.response_dto.UserResponseDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    private final AppointmentRepository appointmentRepository;

    public UserService(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public UserDetails findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponseDTO getUserWithAppointments(Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NoSuchElementException("Usuário não encontrado");
        }
        List<Appointment> appointments = appointmentRepository.findByUserId(id);
        return new UserResponseDTO(user, appointments);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Usuário não encontrado, remoção não foi realizada");
        }
    }

    public User update(User user) {
        return userRepository.save(user);
    }

}