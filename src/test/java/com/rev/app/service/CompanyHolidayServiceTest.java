package com.rev.app.service;

import com.rev.app.service.impl.CompanyHolidayServiceImpl;

import com.rev.app.dto.CompanyHolidayDTO;
import com.rev.app.entity.CompanyHoliday;
import com.rev.app.mapper.CompanyHolidayMapper;
import com.rev.app.repository.CompanyHolidayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyHolidayServiceTest {

    @Mock
    private CompanyHolidayRepository companyHolidayRepository;

    @Mock
    private CompanyHolidayMapper companyHolidayMapper;

    @Mock
    private EmployeeNotificationService notificationService;

    @InjectMocks
    private CompanyHolidayServiceImpl companyHolidayService;

    // Helper to build a DTO from entity fields
    private CompanyHolidayDTO buildDTO(Long id, String name, LocalDate date, String description) {
        CompanyHolidayDTO dto = new CompanyHolidayDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setHolidayDate(date);
        dto.setDescription(description);
        return dto;
    }

    @Test
    void addHoliday_savesHolidayWhenInputIsValid() throws Exception {
        LocalDate holidayDate = LocalDate.of(2026, 1, 26);
        when(companyHolidayRepository.findByHolidayDate(holidayDate)).thenReturn(Optional.empty());
        when(companyHolidayRepository.save(any(CompanyHoliday.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(companyHolidayMapper.toDTO(any(CompanyHoliday.class))).thenAnswer(inv -> {
            CompanyHoliday h = inv.getArgument(0);
            return buildDTO(h.getId(), h.getName(), h.getHolidayDate(), h.getDescription());
        });

        CompanyHolidayDTO saved = companyHolidayService.addHoliday("Republic Day", holidayDate, "National holiday");

        assertEquals("Republic Day", saved.getName());
        assertEquals(holidayDate, saved.getHolidayDate());
        assertEquals("National holiday", saved.getDescription());
        verify(companyHolidayRepository).save(any(CompanyHoliday.class));
    }

    @Test
    void addHoliday_throwsWhenDateAlreadyExists() {
        LocalDate holidayDate = LocalDate.of(2026, 1, 26);
        CompanyHoliday existing = new CompanyHoliday("Republic Day", holidayDate, "Existing");
        existing.setId(1L);

        when(companyHolidayRepository.findByHolidayDate(holidayDate)).thenReturn(Optional.of(existing));

        Exception ex = assertThrows(Exception.class,
                () -> companyHolidayService.addHoliday("Another Holiday", holidayDate, null));

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void updateHoliday_updatesExistingHoliday() throws Exception {
        CompanyHoliday existing = new CompanyHoliday("Old Name", LocalDate.of(2026, 5, 1), "Old");
        existing.setId(10L);
        LocalDate newDate = LocalDate.of(2026, 5, 2);

        when(companyHolidayRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(companyHolidayRepository.findByHolidayDate(newDate)).thenReturn(Optional.empty());
        when(companyHolidayRepository.save(any(CompanyHoliday.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(companyHolidayMapper.toDTO(any(CompanyHoliday.class))).thenAnswer(inv -> {
            CompanyHoliday h = inv.getArgument(0);
            return buildDTO(h.getId(), h.getName(), h.getHolidayDate(), h.getDescription());
        });

        CompanyHolidayDTO updated = companyHolidayService.updateHoliday(10L, "Updated Name", newDate, "Updated");

        assertEquals(10L, updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals(newDate, updated.getHolidayDate());
        assertEquals("Updated", updated.getDescription());
    }

    @Test
    void deleteHoliday_deletesExistingRecord() throws Exception {
        CompanyHoliday existing = new CompanyHoliday("Holiday", LocalDate.of(2026, 8, 15), null);
        existing.setId(22L);

        when(companyHolidayRepository.findById(22L)).thenReturn(Optional.of(existing));

        companyHolidayService.deleteHoliday(22L);

        verify(companyHolidayRepository).delete(existing);
    }

    @Test
    void getHolidaysByYear_readsDateRangeForSelectedYear() {
        CompanyHoliday h1 = new CompanyHoliday("New Year", LocalDate.of(2026, 1, 1), null);
        CompanyHoliday h2 = new CompanyHoliday("Christmas", LocalDate.of(2026, 12, 25), null);

        when(companyHolidayRepository.findByHolidayDateBetweenOrderByHolidayDateAsc(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)))
                .thenReturn(List.of(h1, h2));
        when(companyHolidayMapper.toDTO(h1)).thenReturn(buildDTO(null, "New Year", h1.getHolidayDate(), null));
        when(companyHolidayMapper.toDTO(h2)).thenReturn(buildDTO(null, "Christmas", h2.getHolidayDate(), null));

        List<CompanyHolidayDTO> holidays = companyHolidayService.getHolidaysByYear(2026);

        assertEquals(2, holidays.size());
        assertEquals("New Year", holidays.get(0).getName());
        assertEquals("Christmas", holidays.get(holidays.size() - 1).getName());
    }
}


