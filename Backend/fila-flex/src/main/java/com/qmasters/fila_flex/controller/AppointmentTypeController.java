package com.qmasters.fila_flex.controller;

import com.qmasters.fila_flex.dto.AppointmentTypeDTO;
import com.qmasters.fila_flex.service.AppointmentTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/appointment-types")
public class AppointmentTypeController {

    private final AppointmentTypeService service;

    public AppointmentTypeController(AppointmentTypeService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AppointmentTypeDTO criar(@RequestBody AppointmentTypeDTO dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<AppointmentTypeDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public AppointmentTypeDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
    
}
