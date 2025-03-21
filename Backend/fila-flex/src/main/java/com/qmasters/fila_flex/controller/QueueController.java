package com.qmasters.fila_flex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.model.Appointment;
import com.qmasters.fila_flex.service.QueueService;

@RestController
@RequestMapping("/queue")
public class QueueController {
    @Autowired
    private QueueService queueService;

    //Retorna a fila de agendamentos para um tipo específico pelo nome.
    @GetMapping("/appointment-type/{name}")
    public ResponseEntity<?> getQueueByAppointmentType(@PathVariable String name) {
        try {
            List<Appointment> queue = queueService.getQueueByName(name);
            if (queue.isEmpty()) {
                return ResponseEntity.ok("Não existem agendamentos na fila para este tipo de serviço");
            }
            return ResponseEntity.ok(queue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter a fila: " + e.getMessage());
        }
    }

    //Reordena um agendamento na fila, movendo para uma nova posição.
    @PutMapping("/{id}/position/{position}")
    public ResponseEntity<?> reorderQueue(@PathVariable Long id, @PathVariable Integer position) {
        try {
            queueService.reorderQueue(id, position);
            return ResponseEntity.ok("Agendamento reordenado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao reordenar: " + e.getMessage());
        }
    }
}
