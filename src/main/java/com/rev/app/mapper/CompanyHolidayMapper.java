package com.rev.app.mapper;

import com.rev.app.dto.CompanyHolidayDTO;
import com.rev.app.entity.CompanyHoliday;
import org.springframework.stereotype.Component;

@Component
public class CompanyHolidayMapper {

    public CompanyHolidayDTO toDTO(CompanyHoliday holiday) {
        if (holiday == null) {
            return null;
        }
        CompanyHolidayDTO dto = new CompanyHolidayDTO();
        dto.setId(holiday.getId());
        dto.setName(holiday.getName());
        dto.setHolidayDate(holiday.getHolidayDate());
        dto.setDescription(holiday.getDescription());
        return dto;
    }

    public CompanyHoliday toEntity(CompanyHolidayDTO dto) {
        if (dto == null) {
            return null;
        }
        CompanyHoliday holiday = new CompanyHoliday();
        holiday.setId(dto.getId());
        holiday.setName(dto.getName());
        holiday.setHolidayDate(dto.getHolidayDate());
        holiday.setDescription(dto.getDescription());
        return holiday;
    }
}
