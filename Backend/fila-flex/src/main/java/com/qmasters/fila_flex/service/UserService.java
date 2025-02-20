package com.qmasters.fila_flex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.qmasters.fila_flex.dto.UserDTO;
import com.qmasters.fila_flex.model.User;
import com.qmasters.fila_flex.repository.UserRepository;

public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    public User saveUser(UserDTO userDto) {
        User user = new User(userDto.getUsername()
        , userDto.getPassword()
        , User.Role.valueOf(userDto.getRole()));
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
        }

        public List<User> findAll() {
        return userRepository.findAll();
        }

        public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

}
