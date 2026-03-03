package com.rev.app.repository;

import com.rev.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

<<<<<<< HEAD
    // Native Oracle query to avoid any Hibernate dialect/case-sensitivity issues
    @Query(value = "SELECT * FROM employees WHERE UPPER(TRIM(email)) = UPPER(TRIM(:email)) AND ROWNUM = 1", nativeQuery = true)
    Optional<Employee> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT MAX(e.id) FROM Employee e")
    Long findMaxId();

    List<Employee> findByRoleIgnoreCase(String role);

    List<Employee> findByActiveTrueOrderByIdAsc();

    long countByDepartmentIgnoreCase(String department);

    List<Employee> findByDepartmentIgnoreCase(String department);

    long countByDesignationIgnoreCase(String designation);

    List<Employee> findByDesignationIgnoreCase(String designation);

    List<Employee> findByManager_IdAndActiveTrueOrderByIdAsc(Long managerId);

    List<Employee> findByManager_IdOrderByIdAsc(Long managerId);

    Optional<Employee> findByIdAndManager_IdAndActiveTrue(Long id, Long managerId);
=======
    @org.springframework.data.jpa.repository.Query("SELECT MAX(e.id) FROM Employee e")
    Long findMaxId();

    List<Employee> findByRoleIgnoreCase(String role);
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
}
