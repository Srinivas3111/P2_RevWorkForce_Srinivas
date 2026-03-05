package com.rev.app.service;

import com.rev.app.dto.CompanyHolidayDTO;

import java.time.LocalDate;
import java.util.List;

public interface CompanyHolidayService {
    List<CompanyHolidayDTO> getHolidaysByYear(Integer year);

    CompanyHolidayDTO addHoliday(String name, LocalDate holidayDate, String description) throws Exception;

    CompanyHolidayDTO updateHoliday(Long id, String name, LocalDate holidayDate, String description) throws Exception;

    void deleteHoliday(Long id) throws Exception;
}
