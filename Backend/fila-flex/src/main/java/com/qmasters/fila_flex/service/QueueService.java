package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;

import jakarta.transaction.Transactional;

/**
 * Serviço responsável pelo gerenciamento da fila de atendimentos.
 * 
 * Esta classe implementa a lógica de negócio para manipulação da ordem dos agendamentos,
 * permitindo adicionar novos agendamentos ao final da fila, remover agendamentos existentes 
 * com reorganização automática das posições, e reordenar a fila quando necessário.
 * 
 * O QueueService mantém a integridade da numeração de ordem na fila através de transações
 * atômicas que garantem que múltiplas operações de banco de dados sejam executadas como
 * uma única unidade, evitando inconsistências.
 * 
 * Funções principais:
 * - Adicionar ao final da fila
 * - Remover e comprimir a fila
 * - Reordenar posições (movendo para cima ou para baixo)
 * - Consultar a fila ordenada
 */

@Service
public class QueueService {
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentRepository appointmentRepository;

    public QueueService(AppointmentTypeRepository appointmentTypeRepository, AppointmentRepository appointmentRepository) {
        this.appointmentTypeRepository = appointmentTypeRepository;
        this.appointmentRepository = appointmentRepository;
    }

    //Adiciona um appointment ao final da fila.
    public Appointment assignQueuePosition(Appointment appointment) {
        Integer nextQueueNumber = appointmentTypeRepository.findNextQueueNumberForAppointmentType(
                appointment.getAppointmentType().getId());
        appointment.setQueueOrder(nextQueueNumber);
        return appointment;
    }

    //Reorganiza a fila após remoção.
    public void reorganizeQueueAfterRemoval(Long appointmentTypeId, Integer removedPosition) {
        List<Appointment> subsequentAppointments = appointmentTypeRepository
                .findAllWithQueueOrderGreaterThan(appointmentTypeId, removedPosition);
        
        for (Appointment app : subsequentAppointments) {
            app.setQueueOrder(app.getQueueOrder() - 1);
            appointmentRepository.save(app);
        }
    }

    //Reordena um appointment na fila (move para cima ou para baixo).
    @Transactional
    public void reorderQueue(Long appointmentId, Integer newPosition) {
        //Obter e validar o agendamento e posições.
        Appointment appointment = findAppointmentById(appointmentId);
        Long appointmentTypeId = appointment.getAppointmentType().getId();
        Integer currentPosition = appointment.getQueueOrder();
        
        validateNewPosition(appointmentTypeId, newPosition);
        
        //Se a posição for a mesma, não faz nada.
        if (currentPosition.equals(newPosition)) {
            return;
        }
        
        //Processo de reordenação em três passos.
        moveToTemporaryPosition(appointment);
        reorganizeOtherAppointments(appointmentTypeId, currentPosition, newPosition);
        moveToFinalPosition(appointment, newPosition);
    }

    //Método auxiliar para buscar o agendamento por ID.
    private Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment não encontrado"));
    }

    //Método auxiliar para validar a nova posição.
    private void validateNewPosition(Long appointmentTypeId, Integer newPosition) {
        Integer maxPosition = appointmentTypeRepository.findMaxQueueOrderForAppointmentType(appointmentTypeId);
        if (newPosition < 1 || newPosition > maxPosition) {
            throw new IllegalArgumentException("Posição inválida. Deve estar entre 1 e " + maxPosition);
        }
    }

    //Método auxiliar para mover para posição temporária.
    private void moveToTemporaryPosition(Appointment appointment) {
        appointment.setQueueOrder(-999); //Valor temporário negativo.
        appointmentRepository.save(appointment);
    }

    //Método auxiliar para reorganizar os outros agendamentos.
    private void reorganizeOtherAppointments(Long appointmentTypeId, Integer currentPosition, Integer newPosition) {
        if (newPosition > currentPosition) {
            moveAppointmentsDown(appointmentTypeId, currentPosition, newPosition);
        } else {
            moveAppointmentsUp(appointmentTypeId, newPosition, currentPosition);
        }
    }
    
    //Método auxiliar para mover agendamentos para baixo (aumentando posição).
    private void moveAppointmentsDown(Long appointmentTypeId, Integer fromPosition, Integer toPosition) {
        List<Appointment> appointmentsToUpdate = appointmentRepository.findAllWithPositionBetween(
                appointmentTypeId, fromPosition + 1, toPosition);
        
        for (Appointment app : appointmentsToUpdate) {
            app.setQueueOrder(app.getQueueOrder() - 1);
            appointmentRepository.save(app);
        }
    }    

    //Método auxiliar para mover agendamentos para cima (diminuindo posição).
    private void moveAppointmentsUp(Long appointmentTypeId, Integer fromPosition, Integer toPosition) {
        List<Appointment> appointmentsToUpdate = appointmentRepository.findAllWithPositionBetween(
                appointmentTypeId, fromPosition, toPosition - 1);
        
        for (Appointment app : appointmentsToUpdate) {
            app.setQueueOrder(app.getQueueOrder() + 1);
            appointmentRepository.save(app);
        }
    }
    
    //Método auxiliar para mover para posição final.
    private void moveToFinalPosition(Appointment appointment, Integer newPosition) {
        appointment.setQueueOrder(newPosition);
        appointmentRepository.save(appointment);
    }

    //Retorna a fila ordenada para um determinado tipo de agendamento.
    public List<Appointment> getQueueByName(String appointmentTypeName) {
        return appointmentTypeRepository.findByAppointmentTypeNameOrderByQueueOrder(appointmentTypeName);
    }
}
