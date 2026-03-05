package com.rev.app.security;

import com.rev.app.entity.Employee;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads employee details by email for Spring Security authentication.
 * Roles are prefixed with "ROLE_" to match Spring Security conventions.
 */
@Service
public class EmployeeUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository
                .findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Employee not found with email: " + email));

        String role = "ROLE_" + employee.getRole().toUpperCase();

        return User.builder()
                .username(employee.getEmail())
                // Password is stored as plain-text in DB; Spring Security
                // will compare with PasswordEncoder. We use NoOpPasswordEncoder
                // so we pass the raw value here.
                .password(employee.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(!employee.isActive())
                .credentialsExpired(false)
                .disabled(!employee.isActive())
                .build();
    }
}
