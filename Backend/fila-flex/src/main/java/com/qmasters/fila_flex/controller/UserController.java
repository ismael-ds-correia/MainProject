package com.qmasters.fila_flex.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.response_dto.UserResponseDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    //falta implementar o método de atualização de usuário
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
        
    @GetMapping("/find-id/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        var user = userService.findById(id);
        if (user == null) {
            throw new NoSuchElementException("Usuário não encontrado");
        }
        return ResponseEntity.ok(userService.getUserWithAppointments(id));
    }

    @DeleteMapping("/delete-id/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body("Usuário removido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
