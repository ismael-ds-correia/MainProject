package com.qmasters.fila_flex.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.dto.SimpleAppointmentDTO;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.repository.AppointmentRepository;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public Appointment saveAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment(appointmentDTO.getAppointmentType()
        , appointmentDTO.getUser()
        , appointmentDTO.getScheduledDateTime());
        
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointment() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<SimpleAppointmentDTO> findByScheduledDateTime(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByScheduledDateTime(startDate, endDate).stream()
            .map(this::toSimpleDTO)
            .collect(Collectors.toList());
    }

    //necessario para a função findByDateBetween / converte um tipo Appointment para SimpleAppointmentDTO
    private SimpleAppointmentDTO toSimpleDTO(Appointment appointment) {
        return new SimpleAppointmentDTO(
            appointment.getAppointmentType().getName(),
            appointment.getUser().getEmail(),
            appointment.getScheduledDateTime()
        );
    }

    //função de busca por filtro de usuário
    //public List<Appointment> findAppointmentByUserId(Long user)
    
    @Transactional
    public void deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Agendamento não encontrado, remoção não foi realizada");
        }

    }

}
