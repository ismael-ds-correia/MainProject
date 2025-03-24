package com.qmasters.fila_flex.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.exception.CannotFindQueueException;
import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.service.QueueService;

@RestController
@RequestMapping("/queue")
public class QueueController {
    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    //Retorna a fila de agendamentos para um tipo específico pelo nome.
    @GetMapping("/appointment-type/{name}")
    public ResponseEntity<List<Appointment>> getQueueByAppointmentType(@PathVariable String name) {
        try {
            List<Appointment> queue = queueService.getQueueByName(name);
            if (queue.isEmpty()) {
                throw new NoSuchElementException("Não existem agendamentos na fila para este tipo de serviço");
            }
            return ResponseEntity.ok(queue);
        }catch (NoSuchElementException e) {
            throw e;
        }catch(Exception e) {
            throw new CannotFindQueueException("Erro ao buscar fila de agendamentos: " + e.getMessage());
        }
    }

    //Reordena um agendamento na fila, movendo para uma nova posição.
    @PutMapping("/{id}/position/{position}")
    public ResponseEntity<String> reorderQueue(@PathVariable Long id, @PathVariable Integer position) {
        try {
            queueService.reorderQueue(id, position);
            return ResponseEntity.ok("Agendamento reordenado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao reordenar: " + e.getMessage());
        }
    }
    @PutMapping("/queue/{appointmentTypeId}/next")
    public ResponseEntity<String> callNextInQueue(@PathVariable Long appointmentTypeId) {
        try {
            queueService.callNextInQueue(appointmentTypeId); // Chama o método para movimentar a fila
            return ResponseEntity.ok("Próximo agendamento chamado com sucesso.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao chamar o próximo na fila: " + e.getMessage());
        }
    }

    @GetMapping("/in-queue")
    public ResponseEntity<List<Appointment>> getAppointmentsInQueue() {
    List<Appointment> appointments = queueService.getAppointmentsInQueue();
    if (appointments.isEmpty()) {
        return ResponseEntity.noContent().build(); // Retorna 204 se não houver agendamentos
    }
    return ResponseEntity.ok(appointments); // Retorna a lista com status 200 OK
}
    
}
