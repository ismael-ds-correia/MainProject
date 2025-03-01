package com.qmasters.fila_flex.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.service.AppointmentTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointment-types")
public class AppointmentTypeController {

    private final AppointmentTypeService service;

    public AppointmentTypeController(AppointmentTypeService service) {
        this.service = service;
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public AppointmentTypeDTO toCreate(@Valid @RequestBody AppointmentTypeDTO dto) {
        return service.toCreate(dto);
    }

    @GetMapping("/all")
    public List<AppointmentTypeDTO> listAll() {
        return service.listAll();
    }

    //Endpoint para buscar AppointmentType por categoria.
    @GetMapping("/category")
    public List<AppointmentTypeDTO> findByCategory(@RequestParam String category) {
        return service.findByCategory(category);
    }

    @GetMapping("/{id}")
    public AppointmentTypeDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
}
