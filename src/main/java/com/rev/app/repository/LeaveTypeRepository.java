package com.rev.app.repository;

import com.rev.app.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {

    Optional<LeaveType> findByLeaveName(String leaveName);

    boolean existsByLeaveName(String leaveName);

    java.util.List<LeaveType> findByApplicableGenderInOrApplicableGenderIsNull(java.util.Collection<String> genders);
}
