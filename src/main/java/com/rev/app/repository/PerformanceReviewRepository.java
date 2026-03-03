package com.rev.app.repository;

import com.rev.app.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

    List<PerformanceReview> findByManager_IdOrderBySubmittedOnDescIdDesc(Long managerId);

    List<PerformanceReview> findByEmployee_IdOrderBySubmittedOnDescIdDesc(Long employeeId);

    List<PerformanceReview> findByEmployee_IdOrderBySubmittedOnDesc(Long employeeId);

    List<PerformanceReview> findByManager_IdAndStatusNotIgnoreCaseOrderBySubmittedOnDesc(Long managerId, String status);

    Optional<PerformanceReview> findByEmployee_IdAndReviewPeriodIgnoreCase(Long employeeId, String reviewPeriod);

    Optional<PerformanceReview> findByIdAndEmployee_Id(Long id, Long employeeId);

    Optional<PerformanceReview> findByIdAndManager_Id(Long id, Long managerId);
}
