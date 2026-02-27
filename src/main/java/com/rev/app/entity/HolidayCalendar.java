package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "holiday_calendar")
public class HolidayCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer holidayId;

    private LocalDate holidayDate;

    private String description;

    public HolidayCalendar() {
    }

    public HolidayCalendar(LocalDate holidayDate, String description) {
        this.holidayDate = holidayDate;
        this.description = description;
    }

    public Integer getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(Integer holidayId) {
        this.holidayId = holidayId;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
