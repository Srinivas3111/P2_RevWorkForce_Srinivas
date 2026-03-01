package com.rev.app.mapper;

import com.rev.app.dto.DepartmentDTO;
import com.rev.app.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentDTO toDTO(Department department) {
        if (department == null)
            return null;
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        return dto;
    }

    public Department toEntity(DepartmentDTO dto) {
        if (dto == null)
            return null;
        Department department = new Department();
        department.setName(dto.getName());
        return department;
    }
}
