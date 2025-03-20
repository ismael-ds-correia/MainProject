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
    @Transactional
    public Appointment addToQueue(Appointment appointment) {
        //Obtendo o próximo número da fila.
        Integer nextQueueNumber = appointmentTypeRepository.findNextQueueNumberForAppointmentType(
                appointment.getAppointmentType().getId());
        
        //Atribuindo o número ao appointment.
        appointment.setQueueOrder(nextQueueNumber);
        
        return appointment;
    }

    //Remove um appointment da fila e reorganiza as posições.
    @Transactional
    public void removeFromQueue(Long appointmentId) {
        //Buscando o appointment.
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment não encontrado"));
        
        Long appointmentTypeId = appointment.getAppointmentType().getId();
        Integer currentOrder = appointment.getQueueOrder();
        
        //Removendo o appointment.
        appointmentRepository.delete(appointment);
        
        //Buscando todos os appointments com ordem maior.
        List<Appointment> subsequentAppointments = appointmentTypeRepository
                .findAllWithQueueOrderGreaterThan(appointmentTypeId, currentOrder);
        
        //Decrementando a ordem de cada um.
        for (Appointment app : subsequentAppointments) {
            app.setQueueOrder(app.getQueueOrder() - 1);
            appointmentRepository.save(app);
        }
    }

    //Reordena um appointment na fila (move para cima ou para baixo).
    @Transactional
    public void reorderQueue(Long appointmentId, Integer newPosition) {
        //Buscando o appointment.
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment não encontrado"));
        
        Long appointmentTypeId = appointment.getAppointmentType().getId();
        Integer currentPosition = appointment.getQueueOrder();
        
        //Verificando se a nova posição é válida.
        Integer maxPosition = appointmentTypeRepository.findMaxQueueOrderForAppointmentType(appointmentTypeId);
        if (newPosition < 1 || newPosition > maxPosition) {
            throw new IllegalArgumentException("Posição inválida. Deve estar entre 1 e " + maxPosition);
        }
        
        //Se a posição for a mesma, não faz nada.
        if (currentPosition.equals(newPosition)) {
            return;
        }
        
        //Movendo para baixo (aumentando posição).
        if (newPosition > currentPosition) {
            List<Appointment> appointmentsToUpdate = appointmentRepository.findAllWithPositionBetween(
                    appointmentTypeId, currentPosition + 1, newPosition);
            
            for (Appointment app : appointmentsToUpdate) {
                app.setQueueOrder(app.getQueueOrder() - 1);
                appointmentRepository.save(app);
            }
        } 
        //Movendo para cima (diminuindo posição).
        else {
            List<Appointment> appointmentsToUpdate = appointmentRepository.findAllWithPositionBetween(
                    appointmentTypeId, newPosition, currentPosition - 1);
            
            for (Appointment app : appointmentsToUpdate) {
                app.setQueueOrder(app.getQueueOrder() + 1);
                appointmentRepository.save(app);
            }
        }
        
        //Atualizando a posição do appointment.
        appointment.setQueueOrder(newPosition);
        appointmentRepository.save(appointment);
    }

    //Retorna a fila ordenada para um determinado tipo de agendamento.
    public List<Appointment> getQueueByName(String appointmentTypeName) {
        return appointmentTypeRepository.findByAppointmentTypeNameOrderByQueueOrder(appointmentTypeName);
    }
}
