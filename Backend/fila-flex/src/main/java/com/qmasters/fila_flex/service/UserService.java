package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    //talvez seja redundante, tambem existe a mesma função em AuthService
    public User saveUser(UserDTO userDTO) {
        UserDetails existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        String encriptedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        //userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        User user = new User(userDTO.getEmail(), encriptedPassword, userDTO.getRole(), userDTO.getName());
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public UserDetails findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

}