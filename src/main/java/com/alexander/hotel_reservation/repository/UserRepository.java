package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // find user by email
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    // login query
    @Query(value = "SELECT * FROM users WHERE email = :email and password = :password", nativeQuery = true)
    Optional<User> login(@Param("email") String email,
                         @Param("password") String password);

    // insert user manually
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users(name,email,password,role) VALUES(:name,:email,:password,:role)", nativeQuery = true)
    void insertUser(@Param("name") String name,
                    @Param("email") String email,
                    @Param("password") String password,
                    @Param("role") String role);
}