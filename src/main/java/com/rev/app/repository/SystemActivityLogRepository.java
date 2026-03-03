package com.rev.app.repository;

import com.rev.app.entity.SystemActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemActivityLogRepository extends JpaRepository<SystemActivityLog, Long> {
    List<SystemActivityLog> findAllByOrderByCreatedOnDesc();
}
