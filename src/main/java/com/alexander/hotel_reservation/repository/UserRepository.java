package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // find user by email
    @Query(value = "SELECT * FROM users WHERE email = :email",
            nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);



    // get all users
    @Query(value = "SELECT * FROM users",
            nativeQuery = true)
    List<User> getAllUsers();



    // get user by id
    @Query(value = "SELECT * FROM users WHERE id = :id",
            nativeQuery = true)
    Optional<User> getUserById(@Param("id") Long id);



    // insert user
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users(name,email,password,role) " +
            "VALUES(:name,:email,:password,:role)",
            nativeQuery = true)
    void insertUser(
            @Param("name") String name,
            @Param("email") String email,
            @Param("password") String password,
            @Param("role") String role
    );



    // update user
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET " +
            "name = :name, " +
            "email = :email, " +
            "password = :password, " +
            "role = :role " +
            "WHERE id = :id",
            nativeQuery = true)
    void updateUser(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("email") String email,
            @Param("password") String password,
            @Param("role") String role
    );



    // delete user
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE id = :id",
            nativeQuery = true)
    void deleteUser(@Param("id") Long id);

}