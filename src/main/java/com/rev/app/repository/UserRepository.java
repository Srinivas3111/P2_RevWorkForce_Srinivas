package com.rev.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rev.app.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE (u.email = :identifier OR u.employeeId = :identifier) AND u.passwordHash = :passwordHash")
    List<User> findByIdentifierAndPasswordHash(
            @org.springframework.data.repository.query.Param("identifier") String identifier,
            @org.springframework.data.repository.query.Param("passwordHash") String passwordHash);

    Optional<User> findByEmailAndPasswordHash(String email, String passwordHash);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}