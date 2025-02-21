package com.qmasters.fila_flex.dto;

import com.qmasters.fila_flex.util.UserRole;

public class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private UserRole role;
    
        // Getters and setters
        public UserRole getRole() {
            return role;
        }
    
        public void setRole(UserRole role) {
            this.role = role;
        }
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public String getEmail() {
            return email;
        }
    
        public void setEmail(String email) {
            this.email = email;
        }
    
        public String getPassword() {
            return password;
        }
    
        public void setPassword(String password) {
            this.password = password;
        }
    }

