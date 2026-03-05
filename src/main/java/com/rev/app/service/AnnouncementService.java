package com.rev.app.service;

import com.rev.app.dto.AnnouncementDTO;

import java.util.List;

public interface AnnouncementService {
    List<AnnouncementDTO> getAllAnnouncements();

    List<AnnouncementDTO> getActiveAnnouncements();

    AnnouncementDTO createAnnouncement(String title, String message, boolean active) throws Exception;

    AnnouncementDTO updateAnnouncement(Long announcementId, String title, String message, boolean active) throws Exception;

    void deleteAnnouncement(Long announcementId) throws Exception;
}
