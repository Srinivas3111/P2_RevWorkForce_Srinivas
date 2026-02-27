package com.rev.app.repository;

import com.rev.app.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {
    List<ActivityLog> findAllByOrderByCreatedAtDesc();

    void deleteByUser(com.rev.app.entity.User user);
}
