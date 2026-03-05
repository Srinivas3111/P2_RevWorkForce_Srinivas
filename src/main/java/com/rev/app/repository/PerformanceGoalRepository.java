package com.rev.app.repository;

import com.rev.app.entity.PerformanceGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, Long> {

    List<PerformanceGoal> findByManager_IdOrderByTargetDateAscIdAsc(Long managerId);

    List<PerformanceGoal> findByEmployee_IdOrderByCreatedOnDescIdDesc(Long employeeId);

    boolean existsByEmployee_IdAndGoalDescriptionIgnoreCase(Long employeeId, String goalDescription);

    Optional<PerformanceGoal> findByIdAndEmployee_Id(Long id, Long employeeId);

    Optional<PerformanceGoal> findByIdAndManager_Id(Long id, Long managerId);

    Optional<PerformanceGoal> findByEmployee_IdAndGoalTitleIgnoreCase(Long employeeId, String goalTitle);
}
