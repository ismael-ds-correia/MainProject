package com.qmasters.fila_flex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AdressDTO;
import com.qmasters.fila_flex.service.AdressService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/adress")
public class AdressController {
    
    @Autowired
    private AdressService adressService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdress() {
        return ResponseEntity.ok(adressService.getAllAdress());
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createAdress(@RequestBody AdressDTO adressDTO) {
        try {
            var adress = adressService.saveAdress(adressDTO);
            return ResponseEntity.ok(adress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdressById(@PathVariable Long id) {
        var adress = adressService.findAdressById(id);
        if (adress.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        return ResponseEntity.ok(adress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdressById(@PathVariable Long id) {
        try {
            adressService.deleteAdress(id);
            return ResponseEntity.ok("Endereço removido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
