package com.rev.app.mapper;

import com.rev.app.dto.AnnouncementDTO;
import com.rev.app.entity.Announcement;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public AnnouncementDTO toDTO(Announcement announcement) {
        if (announcement == null)
            return null;
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setMessage(announcement.getMessage());
        dto.setActive(announcement.isActive());
        dto.setCreatedOn(announcement.getCreatedOn());
        dto.setUpdatedOn(announcement.getUpdatedOn());
        return dto;
    }

    public Announcement toEntity(AnnouncementDTO dto) {
        if (dto == null)
            return null;
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setMessage(dto.getMessage());
        announcement.setActive(dto.isActive());
        // ID and Timestamps are usually managed by JPA/Service
        return announcement;
    }
}
