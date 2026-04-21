package com.alexander.hotel_reservation.repository;

import com.alexander.hotel_reservation.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    // select user by email
    @Query(value = "select * from users where email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

    // insert user using sql
    @Modifying
    @Query(value = "insert into users(name, email, password, role) values(:name, :email, :password, :role)", nativeQuery = true)
    void insertUser(@Param("name") String name,
                    @Param("email") String email,
                    @Param("password") String password,
                    @Param("role") String role);

    @Query(value = "SELECT * FROM users WHERE email = :email AND password = :password", nativeQuery = true)
    User login(@Param("email") String email,
               @Param("password") String password);
}