package com.qmasters.fila_flex.dto;
import com.qmasters.fila_flex.util.UserRole;
public class UserDTO {

    private String email;
    private String password;
    private String role;
    private String name;
   
    
    public UserDTO() {
    }

    public UserDTO(String email, String password, UserRole role, String name) {
        this.email = email;
        this.password = password;
        this.role = role.name();
        this.name=name;

    }

    //getters e setters

    public String getName(){
        return name;
    }
    public void setName(String nome){
        this.name=nome;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
}