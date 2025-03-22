package com.qmasters.fila_flex.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public ResponseEntity<List<AppointmentType>> listAll() {        
        return ResponseEntity.ok(appointmentTypeService.listAll());
    }
    
    @PostMapping("/create")
    public ResponseEntity<AppointmentType> saveAppointmentType(@RequestBody AppointmentTypeDTO appointmentTypeDTO) {
        var appointmentType = appointmentTypeService.saveAppointmentType(appointmentTypeDTO);
        return ResponseEntity.ok(appointmentType);
    }

    @GetMapping("/{id}")
    public Optional<AppointmentType> findById(@PathVariable Long id) {
        var appointmentType = appointmentTypeService.findById(id);
        //necessario usar throw, optional retornar vazio não é identificado pelo globalExceptionHandler
        if (appointmentType.isEmpty()) { 
            throw new NoSuchElementException("Tipo de agendamento não encontrado.");
        }
        return appointmentTypeService.findById(id);
    }

    @GetMapping("/category")
    public ResponseEntity<List<AppointmentType>> findByCategory(@RequestParam String category) {
        var appointmentType = appointmentTypeService.findByCategory(category);
        //necessario usar throw, optional retornar vazio não é identificado pelo globalExceptionHandler
        if (appointmentType.isEmpty()) {
            throw new NoSuchElementException("Categoria não encontrada.");
        }
        return ResponseEntity.ok(appointmentType);
    }

    //Endpoint para buscar AppointmentTypes por intervalo de preços.
    @GetMapping("/price-range")
    public ResponseEntity<List<AppointmentType>> findByPriceBetween(@RequestParam double minPrice, @RequestParam double maxPrice) {
        var appointmentType = appointmentTypeService.findByPriceBetween(minPrice, maxPrice);
        //necessario usar throw, optional retornar vazio não é identificado pelo globalExceptionHandler
        if (appointmentType.isEmpty()) {
            throw new NoSuchElementException("Tipos de agendamentos com este preço não foram encontrados.");
        }
        return ResponseEntity.ok(appointmentType);
    }

    @GetMapping("/estimatedTime")
    public List<AppointmentType> findAllByEstimatedTime() {
        return appointmentTypeService.findAllByOrderByEstimatedTimeAsc();
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteByName(@PathVariable String name) {
        try {
            appointmentTypeService.deleteByName(name);
            return ResponseEntity.ok("Tipo de agendamento removido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }    

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            appointmentTypeService.deleteAppointmentType(id);
            return ResponseEntity.ok("Tipo de agendamento removido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Optional<AppointmentType>> findByName(@PathVariable String name) {
        var appointmentType = appointmentTypeService.findByName(name);
        if (appointmentType.isEmpty()) { //é necessario usar throw pois um optional retornar vazio não é identificado como erro pelo globalExceptionHandler
            throw new NoSuchElementException("Tipo de agendamento não encontrado.");
        }
        
        return ResponseEntity.ok(appointmentType);
    }
}
