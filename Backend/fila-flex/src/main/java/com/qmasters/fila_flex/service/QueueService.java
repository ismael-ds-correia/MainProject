package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private  AppointmentTypeRepository appointmentTypeRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

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
        // Buscando o appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment não encontrado"));
        
        Long appointmentTypeId = appointment.getAppointmentType().getId();
        Integer currentPosition = appointment.getQueueOrder();
        
        // Verificando se a nova posição é válida
        Integer maxPosition = appointmentTypeRepository.findMaxQueueOrderForAppointmentType(appointmentTypeId);
        if (newPosition < 1 || newPosition > maxPosition) {
            throw new IllegalArgumentException("Posição inválida. Deve estar entre 1 e " + maxPosition);
        }
        
        // Se a posição for a mesma, não faz nada
        if (currentPosition.equals(newPosition)) {
            return;
        }
        
        // FASE 1: Temporariamente mover o appointment para uma posição negativa
        // para evitar conflitos de chave única durante a reorganização
        appointment.setQueueOrder(-999); // Valor temporário negativo
        appointmentRepository.save(appointment);
        
        // FASE 2: Reorganizar os outros appointments
        if (newPosition > currentPosition) {
            // Movendo para baixo (aumentando posição)
            List<Appointment> appointmentsToUpdate = appointmentRepository.findAllWithPositionBetween(
                    appointmentTypeId, currentPosition + 1, newPosition);
            
            for (Appointment app : appointmentsToUpdate) {
                app.setQueueOrder(app.getQueueOrder() - 1);
                appointmentRepository.save(app);
            }
        } else {
            // Movendo para cima (diminuindo posição)
            List<Appointment> appointmentsToUpdate = appointmentRepository.findAllWithPositionBetween(
                    appointmentTypeId, newPosition, currentPosition - 1);
            
            for (Appointment app : appointmentsToUpdate) {
                app.setQueueOrder(app.getQueueOrder() + 1);
                appointmentRepository.save(app);
            }
        }
        
        // FASE 3: Finalmente, colocar o appointment na sua posição final
        appointment.setQueueOrder(newPosition);
        appointmentRepository.save(appointment);
    }

    //Retorna a fila ordenada para um determinado tipo de agendamento.
    public List<Appointment> getQueueByName(String appointmentTypeName) {
        return appointmentTypeRepository.findByAppointmentTypeNameOrderByQueueOrder(appointmentTypeName);
    }
}
