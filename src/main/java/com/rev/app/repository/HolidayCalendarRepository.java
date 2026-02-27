package com.rev.app.repository;

import com.rev.app.entity.HolidayCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayCalendarRepository extends JpaRepository<HolidayCalendar, Integer> {

    List<HolidayCalendar> findByHolidayDateBetween(LocalDate start, LocalDate end);

    List<HolidayCalendar> findAllByOrderByHolidayDateAsc();

    boolean existsByHolidayDate(LocalDate holidayDate);
}
