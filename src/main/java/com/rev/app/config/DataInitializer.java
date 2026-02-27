package com.rev.app.config;

import com.rev.app.entity.Employee;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.findByEmailIgnoreCase("krishna@revworkforce.com").isEmpty()) {
            // ADMIN
            employeeRepository
                    .save(new Employee(null, "1", "Krishna", "krishna@revworkforce.com", "admin123", "ADMIN"));

            // MANAGERS
            employeeRepository.save(new Employee(null, "2", "Arjun", "arjun@revworkforce.com", "mgr123", "MANAGER"));
            employeeRepository.save(new Employee(null, "3", "Bhimna", "bhimna@revworkforce.com", "mgr123", "MANAGER"));

            // EMPLOYEES
            employeeRepository
                    .save(new Employee(null, "4", "Srinivas", "srinivas@revworkforce.com", "emp123", "EMPLOYEE"));
            employeeRepository
                    .save(new Employee(null, "5", "VenuMadhav", "venumadhav@revworkforce.com", "emp123", "EMPLOYEE"));
            employeeRepository.save(new Employee(null, "6", "Rahul", "rahul@revworkforce.com", "emp123", "EMPLOYEE"));

            System.out.println("Strategic users (Krishna, Arjun, Srinivas, etc.) have been verified/added.");
        }
    }
}
