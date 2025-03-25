package com.qmasters.fila_flex.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @Transactional
    public User register(UserDTO userDTO) {
        UserDetails existingUser = userRepository.findByEmail(userDTO.getEmail());
        
        if (existingUser != null) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        String encriptedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());
    
        User user = new User(userDTO.getEmail(), encriptedPassword, userDTO.getRole(), userDTO.getName());
        return userRepository.save(user);
    }
}