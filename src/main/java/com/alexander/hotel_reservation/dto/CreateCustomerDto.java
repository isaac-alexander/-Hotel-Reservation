package com.alexander.hotel_reservation.dto;

import jakarta.validation.constraints.*;

// dto used by receptionist to create customer
public class CreateCustomerDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "enter valid email")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

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