package com.rev.app.repository;

import com.rev.app.entity.CompanyHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, Long> {

    List<CompanyHoliday> findByHolidayDateBetweenOrderByHolidayDateAsc(LocalDate startDate, LocalDate endDate);

    Optional<CompanyHoliday> findByHolidayDate(LocalDate holidayDate);
}
