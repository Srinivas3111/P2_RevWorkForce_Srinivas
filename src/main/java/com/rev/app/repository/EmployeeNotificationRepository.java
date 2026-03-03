package com.rev.app.repository;

import com.rev.app.entity.EmployeeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeNotificationRepository extends JpaRepository<EmployeeNotification, Long> {

    List<EmployeeNotification> findTop10ByEmployee_IdOrderByCreatedOnDesc(Long employeeId);

    List<EmployeeNotification> findByEmployee_IdOrderByCreatedOnDesc(Long employeeId);

    long countByEmployee_IdAndReadFalse(Long employeeId);

    Optional<EmployeeNotification> findByIdAndEmployee_Id(Long id, Long employeeId);

    List<EmployeeNotification> findByEmployee_IdAndReadFalseOrderByCreatedOnDesc(Long employeeId);
}
