package com.qmasters.fila_flex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.model.AppointmentType;
import com.qmasters.fila_flex.service.AppointmentTypeService;

@RestController
@RequestMapping("/appointment-types")
public class AppointmentTypeController {

    @Autowired
    private AppointmentTypeService appointmentTypeService;
    
    @GetMapping("/all")
    public ResponseEntity<?> listAll() {        
        return ResponseEntity.ok(appointmentTypeService.listAll());
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> saveAppointmentType(@RequestBody AppointmentTypeDTO dto) {
        try {
            var appointmentType = appointmentTypeService.saveAppointmentType(dto);
            return ResponseEntity.ok(appointmentType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public AppointmentType findById(@PathVariable Long id) {
        try {
            return appointmentTypeService.findById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("AppointmentType não encontrado.");
        }
    }

    //Endpoint para buscar AppointmentType por categoria.
    @GetMapping("/category")
    public List<AppointmentTypeDTO> findByCategory(@RequestParam String category) {
        return appointmentTypeService.findByCategory(category);
    }

    //Endpoint para buscar AppointmentTypes por intervalo de preços.
    @GetMapping("/price-range")
    public List<AppointmentTypeDTO> findByPriceBetween(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {

        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice deve ser menor ou igual a maxPrice.");
        }
        
        return appointmentTypeService.findByPriceBetween(minPrice, maxPrice);
    }

    @GetMapping("/estimatedTime")
    public List<AppointmentType> findAllByEstimatedTime() {
        return appointmentTypeService.findAllByOrderByEstimatedTimeAsc();
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable String name) {
        try {
            appointmentTypeService.deleteByName(name);
            return ResponseEntity.ok("Tipo de agendamento removido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            appointmentTypeService.deleteAppointmentType(id);
            return ResponseEntity.ok("Tipo de agendamento removido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<AppointmentType> findByName(@PathVariable String name) {
        try {
            AppointmentType appointmentType = appointmentTypeService.findByName(name);
            return ResponseEntity.ok(appointmentType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
