package com.rev.app.config;

import com.rev.app.entity.Employee;
import com.rev.app.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            LOGGER.info("SEEDER: Syncing default user accounts...");

            // 1. Krishna - ADMIN
            syncUser(1L, "Krishna", "krishna@revworkforce.com", "admin123", "ADMIN", null);

            // 2. Arjun - MANAGER
            syncUser(2L, "Arjun", "arjun@revworkforce.com", "mgr123", "MANAGER", null);

            // 3. Bhima - MANAGER
            syncUser(3L, "Bhima", "bhima@revworkforce.com", "mgr123", "MANAGER", null);

            // 4. Srinivas - EMPLOYEE (reports to Arjun)
            syncUser(4L, "Srinivas", "srinivas@revworkforce.com", "emp123", "EMPLOYEE", 2L);

            // 5. Venumadhav - EMPLOYEE (reports to Bhima)
            syncUser(5L, "Venumadhav", "venumadhav@revworkforce.com", "emp123", "EMPLOYEE", 3L);

            // 6. Rahul - EMPLOYEE (reports to Arjun)
            syncUser(6L, "Rahul", "rahul@revworkforce.com", "emp123", "EMPLOYEE", 2L);

            LOGGER.info("--------------------------------------------------");
            LOGGER.info("SEEDER SUCCESS: All 6 core accounts are synced");
            LOGGER.info("--------------------------------------------------");
        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR during data seeding: {}", e.getMessage(), e);
        }
    }

    private void syncUser(Long id, String fName, String email, String pass, String role,
            Long managerId) {
        try {
            Employee emp = employeeRepository.findById(id).orElse(new Employee());
            emp.setId(id);
            emp.setFirstName(fName);

            emp.setEmail(email);
            emp.setPassword(pass);
            emp.setRole(role);
            emp.setActive(true);
            if (emp.getPhoneNumber() == null) {
                emp.setPhoneNumber("1234567890");
            }

            if (managerId != null) {
                employeeRepository.findById(managerId).ifPresent(emp::setManager);
            }

            Employee saved = employeeRepository.save(emp);
            LOGGER.debug("SEEDER: Saved id={} email={}", saved.getId(), saved.getEmail());
        } catch (Exception ex) {
            LOGGER.error("SEEDER ERROR for id={} email={}: {}", id, email, ex.getMessage(), ex);
        }
    }
}
