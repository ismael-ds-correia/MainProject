package com.qmasters.fila_flex.dto;

public class UserDTO {

    private String email;
    private String password;
    private String role;

    public UserDTO() {
    }

    public UserDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //getters e setters

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
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
