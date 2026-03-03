package com.rev.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "company_holidays", uniqueConstraints = {
        @UniqueConstraint(columnNames = "holiday_date")
})
public class CompanyHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_holiday_seq")
    @SequenceGenerator(name = "company_holiday_seq", sequenceName = "company_holiday_seq", allocationSize = 1)
    private Long id;

    @Column(name = "holiday_name", nullable = false, length = 120)
    private String name;

    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Column(length = 250)
    private String description;

    public CompanyHoliday() {
    }

    public CompanyHoliday(String name, LocalDate holidayDate, String description) {
        this.name = name;
        this.holidayDate = holidayDate;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
