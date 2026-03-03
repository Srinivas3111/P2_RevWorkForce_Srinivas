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
<<<<<<< HEAD
=======
        dto.setLastName(entity.getLastName());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
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
<<<<<<< HEAD
            dto.setManagerName(entity.getManager().getFirstName());
=======
            dto.setManagerName(entity.getManager().getFirstName() + " " + entity.getManager().getLastName());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
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
<<<<<<< HEAD
=======
        entity.setLastName(dto.getLastName());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
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
