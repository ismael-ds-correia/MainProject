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

    //função para buscar Appointment por intervalo de datas.
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
    
    @Transactional
    public Appointment updateAppointment(Long id, AppointmentDTO appointmentDto) {//não passei o AppointmentDTO como parametro, pois só preciso da data e hora
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            LocalDateTime now = LocalDateTime.now();

            if (appointment.getScheduledDateTime().isAfter(now.plusHours(12))) {//se o agendamento vai ocorrer em mais de 12 horas permite reagendar
                LocalDateTime createdDateTime = appointment.getCreatedDateTime();//mantem a data de criação original
                
                appointment.setScheduledDateTime(appointmentDto.getScheduledDateTime());
                appointment.setCreatedDateTime(createdDateTime);
                return appointmentRepository.save(appointment);

            } else {
                throw new IllegalArgumentException("Só é possivel reagendar uma consulta com pelomenos 12 horas de antecedencia.");
            }
        } else {
            throw new IllegalArgumentException("Agendamento não encontrado.");
        }
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Agendamento não encontrado, remoção não foi realizada");
        }

    }

}
