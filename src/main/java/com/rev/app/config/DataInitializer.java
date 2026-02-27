package com.rev.app.config;

import com.rev.app.entity.*;
import com.rev.app.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

        private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private DepartmentRepository departmentRepository;
        @Autowired
        private EmployeeRepository employeeRepository;
        @Autowired
        private LeaveTypeRepository leaveTypeRepository;
        @Autowired
        private LeaveBalanceRepository leaveBalanceRepository;
        @Autowired
        private HolidayCalendarRepository holidayCalendarRepository;
        @Autowired
        private DesignationRepository designationRepository;

        @Override
        public void run(String... args) {
                // Only seed if no users exist
                if (userRepository.count() > 0) {
                        logger.info("Data already exists. Skipping initialization.");
                        return;
                }

                logger.info("Initializing sample data...");

                // ========== DEPARTMENTS ==========
                Department itDept = new Department();
                itDept.setDepartmentName("Information Technology");
                itDept = departmentRepository.save(itDept);

                Department hrDept = new Department();
                hrDept.setDepartmentName("Human Resources");
                hrDept = departmentRepository.save(hrDept);

                Department financeDept = new Department();
                financeDept.setDepartmentName("Finance");
                financeDept = departmentRepository.save(financeDept);

                Department salesDept = new Department();
                salesDept.setDepartmentName("Sales");
                salesDept = departmentRepository.save(salesDept);

                Department marketingDept = new Department();
                marketingDept.setDepartmentName("Marketing");
                marketingDept = departmentRepository.save(marketingDept);

                // ========== LEAVE TYPES ==========
                LeaveType casualLeave = createLeaveType("Casual Leave", 12, "ALL");
                LeaveType sickLeave = createLeaveType("Sick Leave", 10, "ALL");
                LeaveType paidLeave = createLeaveType("Paid Leave", 15, "ALL");
                createLeaveType("Maternity Leave", 180, "FEMALE");
                createLeaveType("Paternity Leave", 15, "MALE");

                // ========== USERS ==========
                User admin = createUser("EMP001", "admin@revworkforce.com", "admin123", "ADMIN");
                User itMgr = createUser("EMP002", "manager@revworkforce.com", "mgr123", "MANAGER");
                User hrMgr = createUser("EMP003", "hrmanager@revworkforce.com", "mgr123", "MANAGER");
                User john = createUser("EMP004", "john@revworkforce.com", "emp123", "EMPLOYEE");
                User jane = createUser("EMP005", "jane@revworkforce.com", "emp123", "EMPLOYEE");
                User mike = createUser("EMP006", "mike@revworkforce.com", "emp123", "EMPLOYEE");
                User sara = createUser("EMP007", "sara@revworkforce.com", "emp123", "EMPLOYEE");
                User ravi = createUser("EMP008", "ravi@revworkforce.com", "emp123", "EMPLOYEE");

                // ========== DESIGNATIONS ==========
                Designation sysAdmin = createDesignation("System Administrator", "Manages system infrastructure");
                Designation itManager = createDesignation("IT Manager", "Manages IT department");
                Designation hrManager = createDesignation("HR Manager", "Manages HR department");
                Designation softwareEngineer = createDesignation("Software Engineer", "Develops software applications");
                Designation seniorDev = createDesignation("Senior Developer", "Senior software developer");
                Designation qaEngineer = createDesignation("QA Engineer", "Quality assurance engineer");
                Designation hrExec = createDesignation("HR Executive", "HR executive");
                Designation accountant = createDesignation("Accountant", "Finance accountant");

                // ========== EMPLOYEES ==========
                Employee adminEmp = createEmployee(admin, "Srinivas", "Admin", "9876543210",
                                "123 Admin St, Hyderabad", "9876543211",
                                LocalDate.of(1985, 1, 15), LocalDate.of(2020, 1, 1),
                                itDept, sysAdmin, null, 150000.0, "MALE");

                Employee itMgrEmp = createEmployee(itMgr, "Rajesh", "Kumar", "9876543220",
                                "456 Tech Park, Bangalore", "9876543221",
                                LocalDate.of(1988, 5, 20), LocalDate.of(2021, 3, 15),
                                itDept, itManager, adminEmp, 120000.0, "MALE");

                Employee hrMgrEmp = createEmployee(hrMgr, "Priya", "Sharma", "9876543230",
                                "789 HR Block, Chennai", "9876543231",
                                LocalDate.of(1990, 8, 10), LocalDate.of(2021, 6, 1),
                                hrDept, hrManager, adminEmp, 110000.0, "FEMALE");

                Employee johnEmp = createEmployee(john, "John", "Doe", "9876543240",
                                "101 Dev Lane, Pune", "9876543241",
                                LocalDate.of(1995, 3, 25), LocalDate.of(2023, 1, 10),
                                itDept, softwareEngineer, itMgrEmp, 75000.0, "MALE");

                Employee janeEmp = createEmployee(jane, "Jane", "Smith", "9876543250",
                                "202 Code Ave, Hyderabad", "9876543251",
                                LocalDate.of(1996, 7, 14), LocalDate.of(2023, 4, 1),
                                itDept, seniorDev, itMgrEmp, 85000.0, "FEMALE");

                Employee mikeEmp = createEmployee(mike, "Mike", "Johnson", "9876543260",
                                "303 Test Rd, Mumbai", "9876543261",
                                LocalDate.of(1994, 11, 5), LocalDate.of(2022, 8, 20),
                                itDept, qaEngineer, itMgrEmp, 65000.0, "MALE");

                Employee saraEmp = createEmployee(sara, "Sara", "Williams", "9876543270",
                                "404 HR St, Delhi", "9876543271",
                                LocalDate.of(1997, 2, 28), LocalDate.of(2024, 1, 5),
                                hrDept, hrExec, hrMgrEmp, 55000.0, "FEMALE");

                Employee raviEmp = createEmployee(ravi, "Ravi", "Patel", "9876543280",
                                "505 Finance Blvd, Bangalore", "9876543281",
                                LocalDate.of(1993, 9, 18), LocalDate.of(2022, 5, 15),
                                financeDept, accountant, hrMgrEmp, 60000.0, "MALE");

                // ========== LEAVE BALANCES ==========
                List<Employee> allEmployees = List.of(adminEmp, itMgrEmp, hrMgrEmp, johnEmp, janeEmp, mikeEmp, saraEmp,
                                raviEmp);
                List<LeaveType> allTypes = leaveTypeRepository.findAll();
                for (Employee emp : allEmployees) {
                        for (LeaveType lt : allTypes) {
                                String gender = lt.getApplicableGender();
                                if (gender == null || "ALL".equalsIgnoreCase(gender)
                                                || gender.equalsIgnoreCase(emp.getGender())) {
                                        leaveBalanceRepository.save(new LeaveBalance(emp, lt, lt.getMaxDaysPerYear()));
                                }
                        }
                }
                logger.info("Leave balances assigned to {} employees", allEmployees.size());

                // ========== HOLIDAYS 2026 ==========
                createHoliday(LocalDate.of(2026, 1, 1), "New Year's Day");
                createHoliday(LocalDate.of(2026, 1, 26), "Republic Day");
                createHoliday(LocalDate.of(2026, 3, 10), "Holi");
                createHoliday(LocalDate.of(2026, 4, 14), "Ambedkar Jayanti");
                createHoliday(LocalDate.of(2026, 5, 1), "May Day");
                createHoliday(LocalDate.of(2026, 8, 15), "Independence Day");
                createHoliday(LocalDate.of(2026, 10, 2), "Gandhi Jayanti");
                createHoliday(LocalDate.of(2026, 10, 20), "Dussehra");
                createHoliday(LocalDate.of(2026, 11, 9), "Diwali");
                createHoliday(LocalDate.of(2026, 12, 25), "Christmas");

                logger.info("Sample data initialized successfully!");
                logger.info("Login credentials:");
                logger.info("  Admin:    admin@revworkforce.com / admin123");
                logger.info("  Manager:  manager@revworkforce.com / mgr123");
                logger.info("  Employee: john@revworkforce.com / emp123");
        }

        private User createUser(String employeeId, String email, String password, String role) {
                User user = new User();
                user.setEmployeeId(employeeId);
                user.setEmail(email);
                user.setPasswordHash(password);
                user.setRole(role);
                user.setActive(true);
                user.setCreatedAt(LocalDateTime.now());
                return userRepository.save(user);
        }

        private Employee createEmployee(User user, String firstName, String lastName,
                        String phone, String address, String emergencyContact,
                        LocalDate dob, LocalDate joiningDate,
                        Department dept, Designation designation,
                        Employee manager, Double salary, String gender) {
                Employee emp = new Employee();
                emp.setUser(user);
                emp.setFirstName(firstName);
                emp.setLastName(lastName);
                emp.setPhone(phone);
                emp.setAddress(address);
                emp.setEmergencyContact(emergencyContact);
                emp.setDob(dob);
                emp.setJoiningDate(joiningDate);
                emp.setDepartment(dept);
                emp.setDesignation(designation);
                emp.setManager(manager);
                emp.setSalary(salary);
                emp.setGender(gender);
                emp.setStatus("ACTIVE");
                return employeeRepository.save(emp);
        }

        private LeaveType createLeaveType(String name, int maxDays, String gender) {
                LeaveType lt = new LeaveType();
                lt.setLeaveName(name);
                lt.setDescription(name + " type");
                lt.setMaxDaysPerYear(maxDays);
                lt.setApplicableGender(gender);
                return leaveTypeRepository.save(lt);
        }

        private Designation createDesignation(String title, String description) {
                Designation d = new Designation(title, description);
                return designationRepository.save(d);
        }

        private void createHoliday(LocalDate date, String description) {
                HolidayCalendar hc = new HolidayCalendar();
                hc.setHolidayDate(date);
                hc.setDescription(description);
                holidayCalendarRepository.save(hc);
        }
}
