package com.qmasters.fila_flex.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AdressDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.service.AdressService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/adress")
public class AdressController {
    
    private final AdressService adressService;

    public AdressController(AdressService adressService) {
        this.adressService = adressService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Adress>> getAllAdress() {
        return ResponseEntity.ok(adressService.getAllAdress());
    }
    
    @PostMapping("/create") //teoricamente o globalException captura erros
    public ResponseEntity<Adress> createAdress(@RequestBody AdressDTO adressDTO) {
        var adress = adressService.saveAdress(adressDTO);
        return ResponseEntity.ok(adress);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Adress>> getAdressById(@PathVariable Long id) {
        var adress = adressService.findAdressById(id);
        //é necessario usar throw pois um optional retornar vazio não é identificado como erro pelo globalExceptionHandler
        if (adress.isEmpty()) {
            throw new NoSuchElementException("Endereco não encontrado"); 
        }
        return ResponseEntity.ok(adress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdressById(@PathVariable Long id) {
        try {
            adressService.deleteAdress(id);
            return ResponseEntity.ok("Endereço removido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
