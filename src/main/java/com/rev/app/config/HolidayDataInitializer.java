package com.rev.app.config;

import com.rev.app.entity.CompanyHoliday;
import com.rev.app.repository.CompanyHolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@Component
@Order(3)
public class HolidayDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayDataInitializer.class);

    @Autowired
    private CompanyHolidayRepository companyHolidayRepository;

    @Override
    public void run(String... args) {
        try {
            int currentYear = Year.now().getValue();
            ensureHoliday("New Year's Day", LocalDate.of(currentYear, 1, 1), "Company holiday");
            ensureHoliday("Republic Day", LocalDate.of(currentYear, 1, 26), "National holiday");
            ensureHoliday("Independence Day", LocalDate.of(currentYear, 8, 15), "National holiday");
            ensureHoliday("Gandhi Jayanti", LocalDate.of(currentYear, 10, 2), "National holiday");
            ensureHoliday("Christmas", LocalDate.of(currentYear, 12, 25), "Company holiday");
        } catch (Exception e) {
            LOGGER.error("HOLIDAY SEED ERROR: {}", e.getMessage(), e);
        }
    }

    private void ensureHoliday(String name, LocalDate date, String description) {
        Optional<CompanyHoliday> existing = companyHolidayRepository.findByHolidayDate(date);
        if (existing.isEmpty()) {
            CompanyHoliday holiday = new CompanyHoliday();
            holiday.setName(name);
            holiday.setHolidayDate(date);
            holiday.setDescription(description);
            companyHolidayRepository.save(holiday);
        }
    }
}
