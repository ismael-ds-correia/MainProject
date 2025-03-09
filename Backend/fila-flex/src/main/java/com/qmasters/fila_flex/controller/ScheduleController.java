package com.qmasters.fila_flex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.ScheduleDTO;
import com.qmasters.fila_flex.service.ScheduleService;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
    
    //falta implementar o método de atualização de usuário
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(scheduleService.findAll());
    }

        
    @PostMapping("/create")
    public ResponseEntity<?> createAdress(@RequestBody ScheduleDTO scheduleDTO) {
        try {
            var adress = scheduleService.saveSchedule(scheduleDTO);
            return ResponseEntity.ok(adress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
        
    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        var schedule = scheduleService.findById(id);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule não encontrado");
        }
        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.status(HttpStatus.OK).body("Schedule removido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
