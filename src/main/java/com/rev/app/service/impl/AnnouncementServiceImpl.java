package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.AnnouncementDTO;
import com.rev.app.entity.Announcement;
import com.rev.app.mapper.AnnouncementMapper;
import com.rev.app.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<AnnouncementDTO> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByUpdatedOnDescCreatedOnDesc()
                .stream()
                .map(announcementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementDTO> getActiveAnnouncements() {
        return announcementRepository.findByActiveTrueOrderByUpdatedOnDescCreatedOnDesc()
                .stream()
                .map(announcementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementDTO createAnnouncement(String title, String message, boolean active) throws Exception {
        Announcement announcement = new Announcement();
        announcement.setTitle(normalizeTitle(title));
        announcement.setMessage(normalizeMessage(message));
        announcement.setActive(active);
        Announcement saved = announcementRepository.save(announcement);

        if (saved.isActive()) {
            notificationService.createNotificationForRole(
                    "EMPLOYEE",
                    "New Announcement Added",
                    "Action: Announcement Added | Employee: All Employees | Announcement: " + saved.getTitle());
        }

        return announcementMapper.toDTO(saved);
    }

    @Override
    public AnnouncementDTO updateAnnouncement(Long announcementId, String title, String message, boolean active)
            throws Exception {
        if (announcementId == null) {
            throw new Exception("Announcement id is required");
        }

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new Exception("Announcement not found"));

        announcement.setTitle(normalizeTitle(title));
        announcement.setMessage(normalizeMessage(message));
        announcement.setActive(active);
        return announcementMapper.toDTO(announcementRepository.save(announcement));
    }

    @Override
    public void deleteAnnouncement(Long announcementId) throws Exception {
        if (announcementId == null) {
            throw new Exception("Announcement id is required");
        }

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new Exception("Announcement not found"));
        announcementRepository.delete(announcement);
    }

    private String normalizeTitle(String title) throws Exception {
        String cleanTitle = (title == null) ? "" : title.trim();
        if (cleanTitle.isEmpty()) {
            throw new Exception("Announcement title is required");
        }
        if (cleanTitle.length() > 160) {
            throw new Exception("Announcement title is too long");
        }
        return cleanTitle;
    }

    private String normalizeMessage(String message) throws Exception {
        String cleanMessage = (message == null) ? "" : message.trim();
        if (cleanMessage.isEmpty()) {
            throw new Exception("Announcement message is required");
        }
        if (cleanMessage.length() > 2000) {
            throw new Exception("Announcement message is too long");
        }
        return cleanMessage;
    }
}



