package com.qmasters.fila_flex.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AppointmentTypeDetailsDTO;
import com.qmasters.fila_flex.model.AppointmentTypeDetails;
import com.qmasters.fila_flex.service.AppointmentTypeDetailsService;

@RestController
@RequestMapping("/appointment-type-details")
public class AppointmentTypeDetailsController {
    private final AppointmentTypeDetailsService appointmentTypeDetailsService;

    public AppointmentTypeDetailsController(AppointmentTypeDetailsService appointmentTypeDetailsService) {
        this.appointmentTypeDetailsService = appointmentTypeDetailsService;
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentTypeDetails>> listAll() {        
        return ResponseEntity.ok(appointmentTypeDetailsService.listAll());
    }

    @PostMapping("/create")
    public ResponseEntity<AppointmentTypeDetails> saveAppointmentTypeDetails(@RequestBody AppointmentTypeDetailsDTO appointmentTypeDetailsDTO) {
        var appointmentTypeDetails = appointmentTypeDetailsService.saveAppointmentTypeDetails(appointmentTypeDetailsDTO);
        return ResponseEntity.ok(appointmentTypeDetails);
    }

    @GetMapping("/find-id/{id}")
    public Optional<AppointmentTypeDetails> findById(@PathVariable Long id) {
        var appointmentTypeDetails = appointmentTypeDetailsService.findById(id);

        if (appointmentTypeDetails.isEmpty()) {
            throw new NoSuchElementException("Detalhes do Tipo de agendamento não encontrado.");
        }
        return appointmentTypeDetailsService.findById(id);
    }

    @DeleteMapping("/delete-id/{id}")
    public ResponseEntity<String> deleteAppointmentTypeDetails(@PathVariable Long id) {
        try {
            appointmentTypeDetailsService.deleteAppointmentTypeDetails(id);
            return ResponseEntity.ok("Detalhes do Tipo de agendamento deletado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
