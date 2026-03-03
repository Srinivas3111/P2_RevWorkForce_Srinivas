package com.rev.app.service;

import com.rev.app.dto.AnnouncementDTO;
import com.rev.app.entity.Announcement;
import com.rev.app.mapper.AnnouncementMapper;
import com.rev.app.repository.AnnouncementRepository;
import com.rev.app.service.impl.AnnouncementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private AnnouncementMapper announcementMapper;

    @Mock
    private EmployeeNotificationService notificationService;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    @Test
    void createAnnouncement_trimsInputAndSendsNotificationWhenActive() throws Exception {
        when(announcementRepository.save(any(Announcement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(announcementMapper.toDTO(any(Announcement.class)))
                .thenAnswer(invocation -> {
                    Announcement source = invocation.getArgument(0);
                    AnnouncementDTO dto = new AnnouncementDTO();
                    dto.setTitle(source.getTitle());
                    dto.setMessage(source.getMessage());
                    dto.setActive(source.isActive());
                    return dto;
                });

        AnnouncementDTO saved = announcementService.createAnnouncement("  Townhall  ", "  Policy update  ", true);

        assertEquals("Townhall", saved.getTitle());
        assertEquals("Policy update", saved.getMessage());
        verify(notificationService).createNotificationForRole(
                eq("EMPLOYEE"),
                eq("New Announcement Added"),
                contains("Townhall"));
    }

    @Test
    void deleteAnnouncement_throwsWhenAnnouncementMissing() {
        when(announcementRepository.findById(88L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> announcementService.deleteAnnouncement(88L));

        assertEquals("Announcement not found", ex.getMessage());
    }
}

