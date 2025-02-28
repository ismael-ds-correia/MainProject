package com.qmasters.fila_flex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.CategoriesDTO;
import com.qmasters.fila_flex.service.CategoriesService;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    @Autowired
    private CategoriesService categoriesService;

    @PostMapping("/add")
    public ResponseEntity<String> addCategory(@RequestBody String categoryName) {
        try {
            CategoriesDTO dto = categoriesService.addCategory(categoryName);
            return ResponseEntity.ok("Categoria adicionada com sucesso: " + dto.getCategoriesNames());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<CategoriesDTO> getAllCategories() {
        CategoriesDTO dto = categoriesService.getAllCategories();
        return ResponseEntity.ok(dto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
