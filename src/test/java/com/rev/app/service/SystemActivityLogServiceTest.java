package com.rev.app.service;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.SystemActivityLog;
import com.rev.app.repository.SystemActivityLogRepository;
import com.rev.app.service.impl.SystemActivityLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemActivityLogServiceTest {

    @Mock
    private SystemActivityLogRepository systemActivityLogRepository;

    @InjectMocks
    private SystemActivityLogServiceImpl systemActivityLogService;

    @Test
    void getRecentLogs_returnsLimitedSubset() {
        SystemActivityLog l1 = new SystemActivityLog();
        SystemActivityLog l2 = new SystemActivityLog();
        SystemActivityLog l3 = new SystemActivityLog();
        when(systemActivityLogRepository.findAllByOrderByCreatedOnDesc()).thenReturn(List.of(l1, l2, l3));

        List<SystemActivityLog> result = systemActivityLogService.getRecentLogs(2);

        assertEquals(2, result.size());
        assertEquals(l1, result.get(0));
        assertEquals(l2, result.get(1));
    }

    @Test
    void logActivity_savesSystemActorWhenActorIsNull() {
        systemActivityLogService.logActivity(null, "  HR  ", "  Update  ", "  Details  ");

        ArgumentCaptor<SystemActivityLog> captor = ArgumentCaptor.forClass(SystemActivityLog.class);
        verify(systemActivityLogRepository).save(captor.capture());

        SystemActivityLog saved = captor.getValue();
        assertEquals("HR", saved.getModuleName());
        assertEquals("Update", saved.getActionName());
        assertEquals("Details", saved.getDetails());
        assertEquals("System", saved.getActorName());
        assertEquals("SYSTEM", saved.getActorRole());
        assertNull(saved.getActorEmail());
    }

    @Test
    void logActivity_truncatesFieldsToConfiguredMaxLength() {
        EmployeeDTO actor = new EmployeeDTO();
        actor.setId(5L);
        actor.setFirstName("A");
        actor.setEmail("longemail@example.com");
        actor.setRole("MANAGER");

        String longModule = "X".repeat(120);
        String longAction = "Y".repeat(200);
        String longDetails = "Z".repeat(2500);

        systemActivityLogService.logActivity(actor, longModule, longAction, longDetails);

        ArgumentCaptor<SystemActivityLog> captor = ArgumentCaptor.forClass(SystemActivityLog.class);
        verify(systemActivityLogRepository).save(captor.capture());

        SystemActivityLog saved = captor.getValue();
        assertEquals(80, saved.getModuleName().length());
        assertEquals(120, saved.getActionName().length());
        assertEquals(2000, saved.getDetails().length());
        assertTrue(saved.getActorName().contains("A"));
    }
}
