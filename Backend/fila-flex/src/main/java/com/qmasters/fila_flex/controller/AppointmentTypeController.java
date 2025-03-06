package com.qmasters.fila_flex.controller;

import java.util.List;

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

    //Endpoint para buscar AppointmentTypes por intervalo de preços.
    @GetMapping("/price-range")
    public List<AppointmentTypeDTO> findByPriceBetween(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {

        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice deve ser menor ou igual a maxPrice.");
        }
        
        return service.findByPriceBetween(minPrice, maxPrice);
    }

    @DeleteMapping("/delete/{name}")
    public void deleteByName(@PathVariable String name) {
        service.deleteByName(name);
    }    

    @GetMapping("/{id}")
    public AppointmentTypeDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
}
