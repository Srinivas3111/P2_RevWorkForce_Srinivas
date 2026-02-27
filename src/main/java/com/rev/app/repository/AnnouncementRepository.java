package com.rev.app.repository;

import com.rev.app.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    List<Announcement> findByCreatedBy(com.rev.app.entity.User user);

    List<Announcement> findAllByOrderByCreatedAtDesc();
}
