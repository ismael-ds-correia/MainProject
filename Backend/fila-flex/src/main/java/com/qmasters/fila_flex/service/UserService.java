package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;

public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

        public List<User> findAll() {
        return userRepository.findAll();
        }

        public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

}
