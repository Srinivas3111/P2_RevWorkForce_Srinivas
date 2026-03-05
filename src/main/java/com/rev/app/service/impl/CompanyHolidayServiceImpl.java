package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.CompanyHolidayDTO;
import com.rev.app.entity.CompanyHoliday;
import com.rev.app.mapper.CompanyHolidayMapper;
import com.rev.app.repository.CompanyHolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyHolidayServiceImpl implements CompanyHolidayService {

    @Autowired
    private CompanyHolidayRepository companyHolidayRepository;

    @Autowired
    private CompanyHolidayMapper companyHolidayMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<CompanyHolidayDTO> getHolidaysByYear(Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        LocalDate startDate = LocalDate.of(selectedYear, 1, 1);
        LocalDate endDate = LocalDate.of(selectedYear, 12, 31);
        return companyHolidayRepository.findByHolidayDateBetweenOrderByHolidayDateAsc(startDate, endDate)
                .stream()
                .map(companyHolidayMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyHolidayDTO addHoliday(String name, LocalDate holidayDate, String description) throws Exception {
        validateHolidayInput(name, holidayDate);

        Optional<CompanyHoliday> sameDateHoliday = companyHolidayRepository.findByHolidayDate(holidayDate);
        if (sameDateHoliday.isPresent()) {
            throw new Exception("A holiday already exists for this date");
        }

        CompanyHoliday holiday = new CompanyHoliday();
        holiday.setName(name.trim());
        holiday.setHolidayDate(holidayDate);
        holiday.setDescription((description == null || description.trim().isEmpty()) ? null : description.trim());
        CompanyHoliday saved = companyHolidayRepository.save(holiday);

        notificationService.createNotificationForRole(
                "EMPLOYEE",
                "New Holiday Added",
                "Action: Holiday Added | Employee: All Employees | Holiday: " + saved.getName()
                        + " | Date: " + saved.getHolidayDate());

        return companyHolidayMapper.toDTO(saved);
    }

    @Override
    public CompanyHolidayDTO updateHoliday(Long id, String name, LocalDate holidayDate, String description)
            throws Exception {
        if (id == null) {
            throw new Exception("Holiday ID is required");
        }
        validateHolidayInput(name, holidayDate);

        CompanyHoliday existingHoliday = companyHolidayRepository.findById(id)
                .orElseThrow(() -> new Exception("Holiday not found"));

        Optional<CompanyHoliday> sameDateHoliday = companyHolidayRepository.findByHolidayDate(holidayDate);
        if (sameDateHoliday.isPresent() && !sameDateHoliday.get().getId().equals(id)) {
            throw new Exception("Another holiday already exists for this date");
        }

        existingHoliday.setName(name.trim());
        existingHoliday.setHolidayDate(holidayDate);
        existingHoliday
                .setDescription((description == null || description.trim().isEmpty()) ? null : description.trim());
        CompanyHoliday saved = companyHolidayRepository.save(existingHoliday);
        return companyHolidayMapper.toDTO(saved);
    }

    @Override
    public void deleteHoliday(Long id) throws Exception {
        if (id == null) {
            throw new Exception("Holiday ID is required");
        }
        CompanyHoliday holiday = companyHolidayRepository.findById(id)
                .orElseThrow(() -> new Exception("Holiday not found"));
        companyHolidayRepository.delete(holiday);
    }

    private void validateHolidayInput(String name, LocalDate holidayDate) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Holiday name is required");
        }
        if (holidayDate == null) {
            throw new Exception("Holiday date is required");
        }
        if (holidayDate.getYear() < 2000 || holidayDate.getYear() > 2100) {
            throw new Exception("Holiday year should be between 2000 and 2100");
        }
    }
}



