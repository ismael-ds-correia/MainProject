package com.qmasters.fila_flex.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmasters.fila_flex.dto.AuthDTO;
import com.qmasters.fila_flex.dto.LoginResponseDTO;
import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.service.AuthService;
import com.qmasters.fila_flex.service.TokenService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final AuthService authService;

    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthDTO authDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
            authDTO.getEmail(), authDTO.getPassword());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        String token = this.tokenService.generateToken((User) auth.getPrincipal());
        
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserDTO userDTO) {
        User user = this.authService.register(userDTO);
        
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
            tokenService.revokeToken(token);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Logout realizado com sucesso");
    }
    
}