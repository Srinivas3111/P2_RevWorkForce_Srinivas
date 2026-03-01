package com.rev.app.mapper;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee entity) {
        if (entity == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setEmergencyContactNumber(entity.getEmergencyContactNumber());
        dto.setGender(entity.getGender());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setAddress(entity.getAddress());
        dto.setDepartment(entity.getDepartment());
        dto.setDesignation(entity.getDesignation());
        dto.setJoiningDate(entity.getJoiningDate());
        dto.setSalary(entity.getSalary());
        dto.setRole(entity.getRole());
        dto.setPassword(entity.getPassword());
        dto.setActive(entity.isActive());

        if (entity.getManager() != null) {
            dto.setManagerId(entity.getManager().getId());
            dto.setManagerName(entity.getManager().getFirstName() + " " + entity.getManager().getLastName());
        }

        return dto;
    }

    public Employee toEntity(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        Employee entity = new Employee();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setEmergencyContactNumber(dto.getEmergencyContactNumber());
        entity.setGender(dto.getGender());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setAddress(dto.getAddress());
        entity.setDepartment(dto.getDepartment());
        entity.setDesignation(dto.getDesignation());
        entity.setJoiningDate(dto.getJoiningDate());
        entity.setSalary(dto.getSalary());
        entity.setRole(dto.getRole());
        entity.setPassword(dto.getPassword());
        entity.setActive(dto.isActive());

        // Note: Manager entity should be resolved by the service layer using the
        // managerId
        return entity;
    }
}
