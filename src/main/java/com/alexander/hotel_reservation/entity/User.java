package com.alexander.hotel_reservation.entity;

import jakarta.persistence.*;

@Entity // tells spring this is a table
@Table(name = "users") // table name in DB
public class User {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)     // ensures no duplicate emails
    private String email;

    private String password;

    private String role; // ADMIN, RECEPTIONIST, CUSTOMER


    public User() {

    }

    public User(Long id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}