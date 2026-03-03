package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees_new")
public class Employee {

    @Id
    @Column(name = "employee_id")
    private Long id;

<<<<<<< HEAD
    @Column(nullable = false)
    private String firstName;
=======
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

<<<<<<< HEAD
    @Column(name = "emergency_contact_number")
    private String emergencyContactNumber;

=======
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    private String department;

    private String designation;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "salary")
    private Double salary;

    @Column(nullable = false)
    private String role; // ADMIN, MANAGER, EMPLOYEE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Column(nullable = false)
    private String password;

    private boolean active = true;

    // Default Constructor
    public Employee() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
<<<<<<< HEAD
    }

    public String getName() {
        return firstName != null ? firstName : "";
=======
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return firstName + " " + lastName;
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

<<<<<<< HEAD
    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

=======
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
<<<<<<< HEAD
        this.password = password != null ? password.trim() : null;
=======
        this.password = password;
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
<<<<<<< HEAD

=======
                ", lastName='" + lastName + '\'' +
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
