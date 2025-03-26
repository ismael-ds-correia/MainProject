package com.qmasters.fila_flex.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.model.enums.AppointmentStatus;
import com.qmasters.fila_flex.repository.AppointmentRepository;
import com.qmasters.fila_flex.repository.AppointmentTypeRepository;
import com.qmasters.fila_flex.util.PriorityCondition;

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
    
    //Retorna a fila ordenada para um determinado tipo de agendamento.
    public List<Appointment> getQueueByName(String appointmentTypeName) {
        return appointmentTypeRepository.findByAppointmentTypeNameOrderByQueueOrder(appointmentTypeName);
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

    @Transactional
    public void insertWithPriority(Long appointmentID) {
        try {
            Appointment appointment = findAppointmentById(appointmentID);
            AppointmentType appointmentType = appointment.getAppointmentType();
            
            //Verifica se há necessidade de reposicionamento.
            if (!shouldRepositionAppointment(appointment, appointmentType)) {
                return;
            }
            
            //Calcula a nova posição ideal para o agendamento prioritário.
            int newPosition = calculatePriorityPosition(appointmentType);
            
            //Aplica a nova posição, respeitando os limites da fila.
            applyNewPosition(appointmentID, newPosition, appointmentType);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar prioridade: " + e.getMessage());
        }
    }

    @Transactional
    public Appointment callNextInQueue(Long appointmentTypeId){
        //Buscando o Appointment com queueOrder = 1 para o AppointmentType especificado
        Appointment next = appointmentRepository.findByAppointmentTypeIdAndQueueOrder(appointmentTypeId, 1);
        
        if (next == null) {
            throw new NoSuchElementException("Não há agendamento na primeira posição da fila para esse tipo de serviço.");
        }
        //Verificando se o appointment está em um status adequado para atendimento (MARKED ou WAITING).
        if (next.getStatus() != AppointmentStatus.MARKED && next.getStatus() != AppointmentStatus.WAITING) {
            throw new IllegalStateException("O agendamento na primeira posição não está disponível para atendimento.");
        }

        //Atualizando o status para ATTENDING e registra o horário de início.
        next.setStatus(AppointmentStatus.ATTENDING);
        next.setStartTime(LocalDateTime.now());
        next.setQueueOrder(0);
        return appointmentRepository.save(next);
    }

    @Transactional
    public Appointment completeAppointment(Long appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);
        
        //Verificando se o agendamento está em atendimento.
        if (appointment.getStatus() != AppointmentStatus.ATTENDING) {
            throw new IllegalStateException("Não é possível finalizar um agendamento que não está em atendimento.");
        }
        
        //Salvando a posição atual para reorganização futura.
        Integer currentPosition = appointment.getQueueOrder();
        Long appointmentTypeId = appointment.getAppointmentType().getId();
        
        //Registrando o término, muda o status e define queueOrder como -1.
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());
        appointment.setQueueOrder(-1);
        
        //Salvando o agendamento atualizado.
        Appointment completedAppointment = appointmentRepository.save(appointment);
        
        //Reorganizando a fila.
        reorganizeQueueAfterRemoval(appointmentTypeId, currentPosition);
        
        return completedAppointment;
    }

    @Transactional
    public Appointment registerCheckIn(Long appointmentId){
        Appointment appointment = findAppointmentById(appointmentId);
        
        //Verificando se o agendamento está marcado.
        if (appointment.getStatus() != AppointmentStatus.MARKED) {
            throw new IllegalStateException("Não é possível registrar check-in para um agendamento que não está marcado.");
        }
        
        //Atualizando o status para WAITING e registra o horário de check-in.
        appointment.setStatus(AppointmentStatus.WAITING);
        appointment.setCheckInTime(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    /*======================== MÉTODOS AUXILIARES ========================*/

    //Método auxiliar para buscar o agendamento por ID.
    private Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado"));
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

    private boolean shouldRepositionAppointment(Appointment appointment, AppointmentType appointmentType) {
        //Se a fila estiver vazia, não reposiciona.
        Integer maxPosition = appointmentTypeRepository.findMaxQueueOrderForAppointmentType(appointmentType.getId());
        if (maxPosition == null || maxPosition == 0) {
            return false;
        }
        
        //Se já estiver em posição privilegiada, não reposiciona.
        int firstNonPriorityPosition = findFirstNonPriorityPosition(appointmentType);
        Integer currentPosition = appointment.getQueueOrder();
        
        return !(currentPosition < firstNonPriorityPosition && 
                 appointment.getPriorityCondition() != PriorityCondition.NO_PRIORITY);
    }    

    private int findFirstNonPriorityPosition(AppointmentType appointmentType) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentTypeIdOrderByQueueOrder(appointmentType.getId());
        
        for (Appointment app : appointments) {
            if (app.getPriorityCondition() == PriorityCondition.NO_PRIORITY) {
                return app.getQueueOrder();
            }
        }
        
        //Se todos têm prioridade, retorna posição após o último.
        Integer maxPosition = appointmentTypeRepository.findMaxQueueOrderForAppointmentType(appointmentType.getId());
        return maxPosition + 1;
    }

    private int calculatePriorityPosition(AppointmentType appointmentType) {
        return findFirstNonPriorityPosition(appointmentType);
    }

    private void applyNewPosition(Long appointmentId, int newPosition, AppointmentType appointmentType) {
        Integer maxPosition = appointmentTypeRepository.findMaxQueueOrderForAppointmentType(appointmentType.getId());
        
        if (newPosition <= maxPosition) {
            reorderQueue(appointmentId, newPosition);
        } else {
            //Se a posição calculada exceder o tamanho da fila, coloca no final.
            reorderQueue(appointmentId, maxPosition);
        }
    }
    
}
