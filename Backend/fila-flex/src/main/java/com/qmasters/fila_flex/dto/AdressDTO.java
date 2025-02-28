package com.qmasters.fila_flex.dto;

public class AdressDTO {
    private String number;
    private String street;
    private String city;
    private String state;
    private String country;

    //getters e setters

    public AdressDTO() {
    }

    public AdressDTO(String number, String street, String city, String state, String country) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
