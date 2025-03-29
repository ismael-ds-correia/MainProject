package com.qmasters.fila_flex.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AppointmentDTO;
import com.qmasters.fila_flex.exception.InvalidDateRangeException;
import com.qmasters.fila_flex.exception.TooLateToChangeException;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.util.PriorityCondition;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private final QueueService queueService;

    public AppointmentService(AppointmentRepository appointmentRepository, QueueService queueService) {
        this.appointmentRepository = appointmentRepository;
        this.queueService = queueService;
    }

    @Transactional
    public Appointment saveAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = new Appointment(
            appointmentDTO.getAppointmentType(), 
            appointmentDTO.getUser(), 
            appointmentDTO.getScheduledDateTime()
        );

        // Atribuir posição na fila
        appointment = queueService.assignQueuePosition(appointment);
        
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointment() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    

    public Appointment setPriorityCondition(Long appointmentId, PriorityCondition priorityCondition) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        if(optionalAppointment.isEmpty()){
            throw new NoSuchElementException("Agendamento não encontrado.");
        } 

        Appointment appointment = optionalAppointment.get();

        appointment.setPriorityCondition(priorityCondition);
        appointmentRepository.save(appointment);

        if (priorityCondition != PriorityCondition.NO_PRIORITY) {
            queueService.insertWithPriority(appointmentId);
        }
        
        return appointment;
    }

    //função para buscar Appointment por intervalo de datas.
    public List<Appointment> findByScheduledDateTime(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Data de início não pode ser posterior a data final");
        } 
        return appointmentRepository.findByScheduledDateTime(startDate, endDate);
    }

    public List<Appointment> findFullAppointmentsByUserId(Long userId) { //talvez função desatualizada
        if (userId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
        return appointmentRepository.findByUserId(userId);
    }
    
    @Transactional//consertar isso daqui
    public Appointment updateAppointment(Long id, AppointmentDTO appointmentDto) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            LocalDateTime now = LocalDateTime.now();

            if (appointmentDto.getScheduledDateTime().isAfter(now.plusHours(12))) {//se o agendamento vai ocorrer em mais de 12 horas permite reagendar
                LocalDateTime createdDateTime = appointment.getCreatedDateTime();//mantem a data de criação original
                
                appointment.setScheduledDateTime(appointmentDto.getScheduledDateTime());
                appointment.setCreatedDateTime(createdDateTime);
                return appointmentRepository.save(appointment);

            } else {
                throw new TooLateToChangeException("Só é possível reagendar uma consulta com pelo menos 12 horas de antecedência.");
            }
        } else {
            throw new NoSuchElementException("Agendamento não encontrado.");
        }
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            //captura informações necessárias para reorganização da fila.
            var appointmentTemp = appointmentRepository.findById(id);
            Long appointmentTypeId = appointmentTemp.get().getAppointmentType().getId();
            Integer queuePosition = appointmentTemp.get().getQueueOrder();
            
            queueService.reorganizeQueueAfterRemoval(appointmentTypeId, queuePosition);
            
            appointmentRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Agendamento não encontrado, remoção não foi realizada");
        }
    }

    // Adicionar ao QueueService.java
    @Transactional
    public Appointment markAsAbsent(Long appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        
        if (appointmentOpt.isEmpty()) {
            throw new NoSuchElementException("Agendamento não encontrado");
        }
        
        Appointment appointment = appointmentOpt.get();
        
        //Verificar se o agendamento não está já concluído ou ausente.
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Não é possível marcar como ausente um agendamento já concluído");
        }
        
        if (appointment.getStatus() == AppointmentStatus.ABSENT) {
            throw new IllegalStateException("Este agendamento já está marcado como ausente");
        }
        
        //Marcar o agendamento como ausente.
        appointment.setStatus(AppointmentStatus.ABSENT);
        appointment.setEndTime(LocalDateTime.now());
        
        //Se este agendamento estava em atendimento, não precisamos reorganizar a fila.
        //Se não, remover da fila e reorganizar os demais.
        if (appointment.getStatus() != AppointmentStatus.ATTENDING && appointment.getQueueOrder() > 0) {
            Long appointmentTypeId = appointment.getAppointmentType().getId();
            Integer queuePosition = appointment.getQueueOrder();
            
            //Zerar a posição na fila para indicar que não está mais na fila.
            appointment.setQueueOrder(0);
            
            //Reorganizar a fila após a remoção.
            queueService.reorganizeQueueAfterRemoval(appointmentTypeId, queuePosition);
        }
        
        return appointmentRepository.save(appointment);
    }
}