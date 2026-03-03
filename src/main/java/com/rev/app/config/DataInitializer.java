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
<<<<<<< HEAD
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
=======
            if (employeeRepository.findByEmailIgnoreCase("krishna@revworkforce.com").isEmpty()) {
                System.out.println("Seeding Strategic Employees into employees_new table...");

                // 1. Krishna - ADMIN
                Employee admin = new Employee();
                admin.setId(1L);
                admin.setFirstName("Krishna");
                admin.setLastName("Admin");
                admin.setEmail("krishna@revworkforce.com");
                admin.setPhoneNumber("0000000000");
                admin.setPassword("admin123");
                admin.setRole("ADMIN");
                employeeRepository.save(admin);

                // 2. Arjun - MANAGER
                Employee mgr1 = new Employee();
                mgr1.setId(2L);
                mgr1.setFirstName("Arjun");
                mgr1.setLastName("Manager");
                mgr1.setEmail("arjun@revworkforce.com");
                mgr1.setPhoneNumber("1111111111");
                mgr1.setPassword("mgr123");
                mgr1.setRole("MANAGER");
                employeeRepository.save(mgr1);

                // 3. Bhima - MANAGER
                Employee mgr2 = new Employee();
                mgr2.setId(3L);
                mgr2.setFirstName("Bhima");
                mgr2.setLastName("Manager");
                mgr2.setEmail("bhima@revworkforce.com");
                mgr2.setPhoneNumber("3333333333");
                mgr2.setPassword("mgr123");
                mgr2.setRole("MANAGER");
                employeeRepository.save(mgr2);

                // 4. Srinivas - EMPLOYEE
                Employee emp1 = new Employee();
                emp1.setId(4L);
                emp1.setFirstName("Srinivas");
                emp1.setLastName("Employee");
                emp1.setEmail("srinivas@revworkforce.com");
                emp1.setPhoneNumber("2222222222");
                emp1.setPassword("emp123");
                emp1.setRole("EMPLOYEE");
                emp1.setManager(mgr1);
                employeeRepository.save(emp1);

                // 5. Venumadhav - EMPLOYEE
                Employee emp2 = new Employee();
                emp2.setId(5L);
                emp2.setFirstName("Venumadhav");
                emp2.setLastName("Employee");
                emp2.setEmail("venumadhav@revworkforce.com");
                emp2.setPhoneNumber("5555555555");
                emp2.setPassword("emp123");
                emp2.setRole("EMPLOYEE");
                emp2.setManager(mgr2);
                employeeRepository.save(emp2);

                // 6. Rahul - EMPLOYEE
                Employee emp3 = new Employee();
                emp3.setId(6L);
                emp3.setFirstName("Rahul");
                emp3.setLastName("Employee");
                emp3.setEmail("rahul@revworkforce.com");
                emp3.setPhoneNumber("6666666666");
                emp3.setPassword("emp123");
                emp3.setRole("EMPLOYEE");
                emp3.setManager(mgr1);
                employeeRepository.save(emp3);

                System.out.println("--------------------------------------------------");
                System.out.println("SUCCESS: Default Accounts (6) Created in New Table!");
                System.out.println("--------------------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR during Data Seeding: " + e.getMessage());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
        }
    }
}
